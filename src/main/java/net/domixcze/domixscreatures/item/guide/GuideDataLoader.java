package net.domixcze.domixscreatures.item.guide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.domixcze.domixscreatures.DomiXsCreatures;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.domixcze.domixscreatures.item.guide.GuideMainScreen.Category;
import static net.domixcze.domixscreatures.item.guide.GuideMainScreen.ImageEntry;
import static net.domixcze.domixscreatures.item.guide.GuideMainScreen.PageContent;
import static net.domixcze.domixscreatures.item.guide.GuideMainScreen.LightweightMenuEntry;

public class GuideDataLoader implements IdentifiableResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String GUIDE_ENTRIES_PATH = "guide_entries";

    private static final Map<Category, List<List<LightweightMenuEntry>>> LOADED_MENU_ENTRIES = new EnumMap<>(Category.class);

    private static final Map<Identifier, List<PageContent>> CACHED_FULL_ENTRIES = new java.util.HashMap<>();


    public static Map<Category, List<List<LightweightMenuEntry>>> getLoadedMenuEntries() {
        return LOADED_MENU_ENTRIES;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(DomiXsCreatures.MOD_ID, "guide_data_loader");
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            prepareProfiler.push("guide_data_prepare");
            loadAllGuideEntries(manager);
            prepareProfiler.pop();
            return null;
        }, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenAcceptAsync((object) -> {
            applyProfiler.push("guide_data_apply");
            applyProfiler.pop();
        }, applyExecutor);
    }

    private void loadAllGuideEntries(ResourceManager manager) {
        CompletableFuture.runAsync(() -> {
            DomiXsCreatures.LOGGER.info("Loading guide book entries...");
            LOADED_MENU_ENTRIES.clear();
            CACHED_FULL_ENTRIES.clear();

            for (Category category : Category.values()) {
                LOADED_MENU_ENTRIES.put(category, new ArrayList<>());
            }

            Map<Identifier, Resource> resources = manager.findResources(GUIDE_ENTRIES_PATH, path -> path.getPath().endsWith(".json"));

            for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
                Identifier id = entry.getKey();
                Resource resource = entry.getValue();

                try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                    GuideEntryJson json = GSON.fromJson(reader, GuideEntryJson.class);

                    if (json.entry_id == null || json.category == null || json.title == null || json.button_text == null) {
                        DomiXsCreatures.LOGGER.warn("Skipping malformed guide entry {}: Missing essential fields (entry_id, category, title, button_text).", id);
                        continue;
                    }

                    Category category = Category.fromString(json.category);
                    if (category == null) {
                        DomiXsCreatures.LOGGER.warn("Skipping guide entry {}: Unknown category '{}'.", id, json.category);
                        continue;
                    }

                    LightweightMenuEntry lightweightEntry = new LightweightMenuEntry(
                            json.button_text,
                            json.title,
                            id,
                            category
                    );

                    List<List<LightweightMenuEntry>> categoryPages = LOADED_MENU_ENTRIES.get(category);
                    if (categoryPages.isEmpty() || categoryPages.getLast().size() >= 8) {
                        categoryPages.add(new ArrayList<>());
                    }
                    categoryPages.getLast().add(lightweightEntry);


                } catch (JsonSyntaxException e) {
                    DomiXsCreatures.LOGGER.error("Failed to parse guide entry JSON {}: {}", id, e.getMessage());
                } catch (IOException e) {
                    DomiXsCreatures.LOGGER.error("Failed to read guide entry file {}: {}", id, e.getMessage());
                } catch (Exception e) {
                    DomiXsCreatures.LOGGER.error("An unexpected error occurred while processing guide entry {}: {}", id, e.getMessage(), e);
                }
            }
            DomiXsCreatures.LOGGER.info("Loaded {} guide entries.", LOADED_MENU_ENTRIES.values().stream().flatMap(List::stream).mapToInt(List::size).sum());
        });
    }

    public static List<PageContent> getEntryContent(Identifier entryJsonId) {
        if (CACHED_FULL_ENTRIES.containsKey(entryJsonId)) {
            return CACHED_FULL_ENTRIES.get(entryJsonId);
        }

        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        List<PageContent> fullContent = new ArrayList<>();

        Optional<Resource> optionalResource = manager.getResource(entryJsonId);
        if (optionalResource.isEmpty()) {
            DomiXsCreatures.LOGGER.error("Guide entry JSON file not found: {}", entryJsonId);
            return List.of();
        }

        try (Reader reader = new InputStreamReader(optionalResource.get().getInputStream())) {
            GuideEntryJson json = GSON.fromJson(reader, GuideEntryJson.class);

            if (json == null || json.pages == null) {
                DomiXsCreatures.LOGGER.error("Parsed guide entry JSON is null or has no pages: {}", entryJsonId);
                return List.of();
            }

            for (PageContentJson pageJson : json.pages) {
                List<Text> textContent = new ArrayList<>();
                if (pageJson.text_content != null) {
                    for (String textLine : pageJson.text_content) {
                        textContent.add(Text.translatable(textLine));
                    }
                }

                List<ImageEntry> images = new ArrayList<>();
                if (pageJson.images != null) {
                    for (ImageEntryJson imgJson : pageJson.images) {
                        try {
                            ImageEntry img = convertImageEntry(imgJson);
                            if (img != null) {
                                images.add(img);
                            }
                        } catch (Exception e) {
                            DomiXsCreatures.LOGGER.warn("Failed to convert image entry for {}: {} - {}", entryJsonId, imgJson.id, e.getMessage());
                        }
                    }
                }
                fullContent.add(new PageContent(textContent, images));
            }
            CACHED_FULL_ENTRIES.put(entryJsonId, fullContent);
            return fullContent;

        } catch (JsonSyntaxException e) {
            DomiXsCreatures.LOGGER.error("Failed to parse full guide entry JSON {}: {}", entryJsonId, e.getMessage());
        } catch (IOException e) {
            DomiXsCreatures.LOGGER.error("Failed to read full guide entry file {}: {}", entryJsonId, e.getMessage());
        } catch (Exception e) {
            DomiXsCreatures.LOGGER.error("An unexpected error occurred while loading full guide entry {}: {}", entryJsonId, e.getMessage(), e);
        }
        return List.of();
    }

    private static GuideMainScreen.ImageEntry convertImageEntry(ImageEntryJson json) {
        if (json.type == null) {
            DomiXsCreatures.LOGGER.warn("Image entry is missing type in JSON: {}", json);
            return null;
        }

        int frameDuration = json.frame_duration_ticks > 0 ? json.frame_duration_ticks : GuideMainScreen.DEFAULT_IMAGE_CYCLE_FRAME_DURATION_TICKS;

        if ("item".equalsIgnoreCase(json.type)) {
            if (json.id == null) {
                DomiXsCreatures.LOGGER.warn("Item image entry missing 'id' in JSON: {}", json);
                return null;
            }
            Identifier id = Identifier.of(json.id);
            Item item = Registries.ITEM.get(id);
            if (item == Items.AIR && !id.equals(Identifier.of("minecraft", "air"))) {
                DomiXsCreatures.LOGGER.warn("Item not found for ID '{}' from JSON. Skipping.", json.id);
                return null;
            }
            return GuideMainScreen.ImageEntry.item(new ItemStack(item), json.x, json.y, json.width, json.height);
        } else if ("texture".equalsIgnoreCase(json.type)) {
            if (json.id == null) {
                DomiXsCreatures.LOGGER.warn("Texture image entry missing 'id' in JSON: {}", json);
                return null;
            }
            Identifier id = Identifier.of(json.id);
            return GuideMainScreen.ImageEntry.texture(id, json.x, json.y, json.width, json.height);
        } else if ("cycling_item".equalsIgnoreCase(json.type)) {
            if (json.ids == null || json.ids.isEmpty()) {
                DomiXsCreatures.LOGGER.warn("Cycling item entry missing 'ids' list in JSON: {}", json);
                return null;
            }
            List<ItemStack> itemStacks = new ArrayList<>();
            for (String itemIdStr : json.ids) {
                Identifier id = Identifier.of(itemIdStr);
                Item item = Registries.ITEM.get(id);
                if (item == Items.AIR && !id.equals(Identifier.of("minecraft", "air"))) {
                    DomiXsCreatures.LOGGER.warn("Item not found for ID '{}' in cycling list. Skipping this frame.", itemIdStr);
                    continue;
                }
                itemStacks.add(new ItemStack(item));
            }
            if (itemStacks.isEmpty()) {
                DomiXsCreatures.LOGGER.warn("No valid items found for cycling_item entry with ID(s): {}. Skipping this entry.", json.ids);
                return null;
            }
            return GuideMainScreen.ImageEntry.cyclingItem(itemStacks, frameDuration, json.x, json.y, json.width, json.height);
        } else if ("cycling_texture".equalsIgnoreCase(json.type)) {
            if (json.ids == null || json.ids.isEmpty()) {
                DomiXsCreatures.LOGGER.warn("Cycling texture entry missing 'ids' list in JSON: {}", json);
                return null;
            }
            List<Identifier> identifiers = new ArrayList<>();
            for (String textureIdStr : json.ids) {
                identifiers.add(Identifier.of(textureIdStr));
            }
            if (identifiers.isEmpty()) {
                DomiXsCreatures.LOGGER.warn("No valid textures found for cycling_texture entry with ID(s): {}. Skipping this entry.", json.ids);
                return null;
            }
            return GuideMainScreen.ImageEntry.cyclingTexture(identifiers, frameDuration, json.x, json.y, json.width, json.height);
        } else {
            DomiXsCreatures.LOGGER.warn("Unknown image type '{}' in JSON for ImageEntry with ID(s): {}. Skipping.", json.type, json.id != null ? json.id : json.ids);
            return null;
        }
    }
}