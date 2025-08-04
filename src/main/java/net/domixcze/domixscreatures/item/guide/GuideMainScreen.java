package net.domixcze.domixscreatures.item.guide;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class GuideMainScreen extends Screen {
    private static final Identifier BOOK_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/gui/two_page_book.png");
    private static final int BOOK_WIDTH = 256;
    private static final int BOOK_HEIGHT = 256;

    private static final int PAGE_WIDTH = BOOK_WIDTH / 2;
    private static final int TEXT_MARGIN_X = 13;
    private static final int TEXT_MARGIN_Y_TOP = 40;
    private static final int TEXT_WIDTH_PER_PAGE = 105;
    private static final int LINE_HEIGHT = 9;

    private static final int LEFT_PAGE_TEXT_X = TEXT_MARGIN_X;
    private static final int RIGHT_PAGE_TEXT_X = PAGE_WIDTH + TEXT_MARGIN_X;
    private static final int TEXT_START_Y = TEXT_MARGIN_Y_TOP;

    private static final int MAX_LINES_PER_VISUAL_PAGE = (BOOK_HEIGHT - TEXT_MARGIN_Y_TOP - 20) / LINE_HEIGHT;

    private static final int BOOKMARK_WIDTH = 64;
    private static final int BOOKMARK_HEIGHT = 16;
    private static final int BOOKMARK_TEXT_OFFSET_X = 15;
    private static final Identifier BOOKMARK_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/gui/bookmark.png");
    private static final Identifier BOOKMARK_HIGHLIGHTED_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/gui/bookmark_highlighted.png");

    private static final Identifier EXIT_PAGE_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/gui/exit_page.png");
    private static final Identifier EXIT_PAGE_HIGHLIGHTED_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/gui/exit_page_highlighted.png");
    private static final Identifier ENTRY_BUTTON_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/gui/entry_button.png");

    private enum PageType {
        MENU,
        ENTRY
    }

    public enum Category {
        ENTITIES(Text.literal("Entities")),
        ITEMS(Text.literal("Items")),
        BLOCKS(Text.literal("Blocks"));

        public final Text bookmarkText;

        Category(Text bookmarkText) {
            this.bookmarkText = bookmarkText;
        }

        public static Category fromString(String name) {
            for (Category category : values()) {
                if (category.name().equalsIgnoreCase(name)) {
                    return category;
                }
            }
            return null;
        }
    }

    private enum ImageType {
        ITEM,
        TEXTURE,
        CYCLING_ITEM,
        CYCLING_TEXTURE
    }

    public record ImageEntry(ImageType type, @Nullable Identifier identifier, @Nullable ItemStack itemStack,
                             @Nullable List<Identifier> cyclingIdentifiers,
                             @Nullable List<ItemStack> cyclingItemStacks,
                             int frameDurationTicks,
                             int x, int y, int width, int height) {

        public static ImageEntry texture(Identifier identifier, int x, int y, int width, int height) {
            return new ImageEntry(ImageType.TEXTURE, identifier, null, null, null, 0, x, y, width, height);
        }

        public static ImageEntry item(ItemStack itemStack, int x, int y, int width, int height) {
            return new ImageEntry(ImageType.ITEM, null, itemStack, null, null, 0, x, y, width, height);
        }

        public static ImageEntry cyclingTexture(List<Identifier> identifiers, int frameDurationTicks, int x, int y, int width, int height) {
            return new ImageEntry(ImageType.CYCLING_TEXTURE, null, null, List.copyOf(identifiers), null, frameDurationTicks, x, y, width, height);
        }

        public static ImageEntry cyclingItem(List<ItemStack> itemStacks, int frameDurationTicks, int x, int y, int width, int height) {
            return new ImageEntry(ImageType.CYCLING_ITEM, null, null, null, List.copyOf(itemStacks), frameDurationTicks, x, y, width, height);
        }
    }

    public static class PageContent {
        private final List<Text> textContent;
        private final List<ImageEntry> images;

        private List<OrderedText> cachedWrappedLines = null;
        private TextRenderer lastUsedRenderer = null;
        private int lastUsedMaxWidth = -1;

        public PageContent(List<Text> textContent, List<ImageEntry> images) {
            this.textContent = List.copyOf(textContent);
            this.images = List.copyOf(images);
        }

        public List<ImageEntry> images() {
            return images;
        }

        public List<OrderedText> getWrappedLines(TextRenderer renderer, int maxWidth) {
            if (cachedWrappedLines == null || renderer != lastUsedRenderer || maxWidth != lastUsedMaxWidth) {
                List<OrderedText> newWrappedLines = new ArrayList<>();
                for (Text line : textContent) {
                    if (line.getString().isEmpty()) {
                        newWrappedLines.add(OrderedText.EMPTY);
                    } else {
                        newWrappedLines.addAll(renderer.wrapLines(line, maxWidth));
                    }
                }
                this.cachedWrappedLines = Collections.unmodifiableList(newWrappedLines);
                this.lastUsedRenderer = renderer;
                this.lastUsedMaxWidth = maxWidth;
            }
            return cachedWrappedLines;
        }
    }

    public record LightweightMenuEntry(Text buttonText, Text entryTitle, Identifier jsonLocation, Category category) {}

    // Screen State Variables
    private PageType currentPage = PageType.MENU;
    private List<PageContent> currentEntryPagesContent = List.of();
    private Text currentTitle = Text.empty();
    private int currentEntryPage = 0;
    private int currentMenuPage = 0;
    private Category currentCategory = Category.ENTITIES;

    private int globalImageCycleTick = 0;
    public static final int DEFAULT_IMAGE_CYCLE_FRAME_DURATION_TICKS = 100;

    // Widgets
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private ButtonWidget backButton;

    // Book Position
    private int bookX;
    private int bookY;

    public GuideMainScreen() {
        super(Text.literal("Guide Book"));
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        globalImageCycleTick++;
    }

    @Override
    protected void init() {
        this.bookX = (this.width - BOOK_WIDTH) / 2;
        this.bookY = 2;

        this.clearChildren();

        if (currentPage == PageType.MENU) {
            Map<Category, List<List<LightweightMenuEntry>>> loadedMenuEntries = GuideDataLoader.getLoadedMenuEntries();
            List<List<LightweightMenuEntry>> pagesForCurrentCategory = loadedMenuEntries.getOrDefault(currentCategory, List.of());

            if (currentMenuPage < pagesForCurrentCategory.size()) {
                List<LightweightMenuEntry> entriesForCurrentMenuPage = pagesForCurrentCategory.get(currentMenuPage);
                int buttonYOffset = 50;
                int buttonSpacing = 30;

                int leftColumnButtonX = bookX + TEXT_MARGIN_X - ((100 - TEXT_WIDTH_PER_PAGE) / 2);
                int rightColumnButtonX = bookX + PAGE_WIDTH + TEXT_MARGIN_X - ((100 - TEXT_WIDTH_PER_PAGE) / 2);

                for (int i = 0; i < entriesForCurrentMenuPage.size(); i++) {
                    LightweightMenuEntry entry = entriesForCurrentMenuPage.get(i);

                    int currentButtonX;
                    int currentButtonY = bookY + buttonYOffset + (i % 4) * buttonSpacing;

                    if (i < 4) {
                        currentButtonX = leftColumnButtonX;
                    } else {
                        currentButtonX = rightColumnButtonX;
                    }

                    this.addDrawableChild(new NavigationButton(
                            currentButtonX,
                            currentButtonY,
                            100,
                            20,
                            entry.buttonText(),
                            (b) -> openEntry(entry.jsonLocation())
                    ));
                }
            }
        }

        int bookmarkYOffset = bookY + 50;
        int bookmarkSpacing = 5;

        for (int i = 0; i < Category.values().length; i++) {
            Category category = Category.values()[i];
            this.addDrawableChild(new BookmarkButton(
                    bookX - BOOKMARK_WIDTH,
                    bookmarkYOffset + i * (BOOKMARK_HEIGHT + bookmarkSpacing),
                    BOOKMARK_WIDTH,
                    BOOKMARK_HEIGHT,
                    category.bookmarkText,
                    category,
                    (button) -> {
                        this.currentCategory = category;
                        this.currentPage = PageType.MENU;
                        this.currentMenuPage = 0;
                        this.init();
                    }
            ));
        }

        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(
                bookX + (PAGE_WIDTH / 2) - 40,
                bookY + 159,
                false,
                (b) -> {
                    if (currentPage == PageType.ENTRY) {
                        if (currentEntryPage == 0) {
                            returnToMenu();
                        } else {
                            previousEntryPage();
                        }
                    } else {
                        previousMenuPage();
                    }
                }, true));

        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(
                bookX + PAGE_WIDTH + (PAGE_WIDTH / 2) + 20,
                bookY + 159,
                true,
                (b) -> {
                    if (currentPage == PageType.ENTRY) {
                        nextEntryPage();
                    } else {
                        nextMenuPage();
                    }
                }, true));

        this.backButton = this.addDrawableChild(new BackButton(
                bookX + TEXT_MARGIN_X - 30,
                bookY + 10,
                18,
                18,
                (button) -> this.close()
        ));

        updateButtonVisibility();
    }

    private void openEntry(Identifier entryJsonId) {
        List<PageContent> pagesContent = GuideDataLoader.getEntryContent(entryJsonId);

        Text entryTitle = Text.literal("Unknown Entry");
        findEntryTitle:
        for (List<LightweightMenuEntry> pageList : GuideDataLoader.getLoadedMenuEntries().getOrDefault(currentCategory, List.of())) {
            for (LightweightMenuEntry lightweightEntry : pageList) {
                if (lightweightEntry.jsonLocation().equals(entryJsonId)) {
                    entryTitle = lightweightEntry.entryTitle();
                    break findEntryTitle;
                }
            }
        }

        this.currentPage = PageType.ENTRY;
        this.currentTitle = entryTitle;
        this.currentEntryPagesContent = pagesContent;
        this.currentEntryPage = 0;
        this.init();
    }

    private void returnToMenu() {
        this.currentPage = PageType.MENU;
        this.currentEntryPagesContent = List.of();
        this.currentTitle = Text.empty();
        this.currentEntryPage = 0;
        this.init();
    }

    @Override
    public void close() {
        super.close();
    }


    private void updateButtonVisibility() {
        boolean isEntryPage = currentPage == PageType.ENTRY;
        boolean isMenuPage = currentPage == PageType.MENU;

        this.previousPageButton.visible = isEntryPage || (isMenuPage && currentMenuPage > 0);

        Map<Category, List<List<LightweightMenuEntry>>> loadedMenuEntries = GuideDataLoader.getLoadedMenuEntries();
        List<List<LightweightMenuEntry>> pagesInCurrentCategory = loadedMenuEntries.getOrDefault(currentCategory, List.of());

        this.nextPageButton.visible = (isEntryPage && currentEntryPage < currentEntryPagesContent.size() - 2) ||
                (isMenuPage && currentMenuPage < pagesInCurrentCategory.size() - 1);

        this.backButton.visible = true;
    }

    private void nextEntryPage() {
        if (currentEntryPage < currentEntryPagesContent.size() - 2) {
            currentEntryPage += 2;
            updateButtonVisibility();
        }
    }

    private void previousEntryPage() {
        if (currentEntryPage > 0) {
            currentEntryPage -= 2;
            updateButtonVisibility();
        }
    }

    private void nextMenuPage() {
        Map<Category, List<List<LightweightMenuEntry>>> loadedMenuEntries = GuideDataLoader.getLoadedMenuEntries();
        List<List<LightweightMenuEntry>> pagesInCurrentCategory = loadedMenuEntries.getOrDefault(currentCategory, List.of());

        if (currentMenuPage < pagesInCurrentCategory.size() - 1) {
            currentMenuPage++;
            this.init();
        }
    }

    private void previousMenuPage() {
        if (currentMenuPage > 0) {
            currentMenuPage--;
            this.init();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (currentPage == PageType.MENU) {
            renderMenu(context);
        } else {
            renderEntry(context);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        context.drawTexture(BOOK_TEXTURE, bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);
    }

    private void renderMenu(DrawContext context) {
        int titleX = bookX + (BOOK_WIDTH - textRenderer.getWidth(Text.literal("Guide Book"))) / 2;
        context.drawText(textRenderer, Text.literal("Guide Book"), titleX, bookY + 10, 0xffffff, false);

        int leftPageNumber = currentMenuPage * 2 + 1;
        int rightPageNumber = currentMenuPage * 2 + 2;
        int numberY = bookY + 165;

        Text leftNumText = Text.literal(String.valueOf(leftPageNumber));
        int leftNumWidth = textRenderer.getWidth(leftNumText);
        int leftNumX = bookX + (PAGE_WIDTH / 2) - (leftNumWidth / 2);
        context.drawText(textRenderer, leftNumText, leftNumX, numberY, 0x503000, false);

        Text rightNumText = Text.literal(String.valueOf(rightPageNumber));
        int rightNumWidth = textRenderer.getWidth(rightNumText);
        int rightNumX = bookX + PAGE_WIDTH + (PAGE_WIDTH / 2) - (rightNumWidth / 2);
        context.drawText(textRenderer, rightNumText, rightNumX, numberY, 0x503000, false);
    }

    private void renderEntry(DrawContext context) {
        int titleX = bookX + (BOOK_WIDTH - textRenderer.getWidth(currentTitle)) / 2;
        context.drawText(textRenderer, currentTitle, titleX, bookY + 10, 0xffffff, false);

        int leftPageNumber = currentEntryPage + 1;
        int rightPageNumber = currentEntryPage + 2;
        int numberY = bookY + 165;

        Text leftNumText = Text.literal(String.valueOf(leftPageNumber));
        int leftNumWidth = textRenderer.getWidth(leftNumText);
        int leftNumX = bookX + (PAGE_WIDTH / 2) - (leftNumWidth / 2);
        context.drawText(textRenderer, leftNumText, leftNumX, numberY, 0x503000, false);

        Text rightNumText = Text.literal(String.valueOf(rightPageNumber));
        int rightNumWidth = textRenderer.getWidth(rightNumText);
        int rightNumX = bookX + PAGE_WIDTH + (PAGE_WIDTH / 2) - (rightNumWidth / 2);
        context.drawText(textRenderer, rightNumText, rightNumX, numberY, 0x503000, false);

        if (currentEntryPagesContent.isEmpty() || currentEntryPage < 0) return;

        // === LEFT PAGE ===
        if (currentEntryPage < currentEntryPagesContent.size()) {
            PageContent leftPage = currentEntryPagesContent.get(currentEntryPage);
            List<OrderedText> leftPageLines = leftPage.getWrappedLines(textRenderer, TEXT_WIDTH_PER_PAGE);

            int drawYLeft = bookY + TEXT_START_Y;
            for (int i = 0; i < Math.min(MAX_LINES_PER_VISUAL_PAGE, leftPageLines.size()); i++) {
                context.drawText(textRenderer, leftPageLines.get(i), bookX + LEFT_PAGE_TEXT_X, drawYLeft, 0x503000, false);
                drawYLeft += LINE_HEIGHT;
            }

            for (ImageEntry img : leftPage.images()) {
                int x = bookX + LEFT_PAGE_TEXT_X + img.x();
                int y = bookY + img.y();

                if (img.type() == ImageType.ITEM && img.itemStack() != null) {
                    renderScaledItem(context, img.itemStack(), x, y, img.width(), img.height());
                } else if (img.type() == ImageType.TEXTURE && img.identifier() != null) {
                    context.drawTexture(img.identifier(),
                            x, y,
                            0, 0,
                            img.width(), img.height(),
                            img.width(), img.height());
                }
                else if (img.type() == ImageType.CYCLING_ITEM && img.cyclingItemStacks() != null && !img.cyclingItemStacks().isEmpty()) {
                    int frameDuration = img.frameDurationTicks() > 0 ? img.frameDurationTicks() : DEFAULT_IMAGE_CYCLE_FRAME_DURATION_TICKS;
                    int currentFrameIndex = (globalImageCycleTick / frameDuration) % img.cyclingItemStacks().size();
                    ItemStack currentStack = img.cyclingItemStacks().get(currentFrameIndex);
                    renderScaledItem(context, currentStack, x, y, img.width(), img.height());
                }
                else if (img.type() == ImageType.CYCLING_TEXTURE && img.cyclingIdentifiers() != null && !img.cyclingIdentifiers().isEmpty()) {
                    int frameDuration = img.frameDurationTicks() > 0 ? img.frameDurationTicks() : DEFAULT_IMAGE_CYCLE_FRAME_DURATION_TICKS;
                    int currentFrameIndex = (globalImageCycleTick / frameDuration) % img.cyclingIdentifiers().size();
                    Identifier currentTexture = img.cyclingIdentifiers().get(currentFrameIndex);

                    context.drawTexture(currentTexture,
                            x, y,
                            0, 0,
                            img.width(), img.height(),
                            img.width(), img.height());
                }
            }
        }

        // === RIGHT PAGE ===
        if (currentEntryPage + 1 < currentEntryPagesContent.size()) {
            PageContent rightPage = currentEntryPagesContent.get(currentEntryPage + 1);
            List<OrderedText> rightPageLines = rightPage.getWrappedLines(textRenderer, TEXT_WIDTH_PER_PAGE);

            int drawYRight = bookY + TEXT_START_Y;
            for (int i = 0; i < Math.min(MAX_LINES_PER_VISUAL_PAGE, rightPageLines.size()); i++) {
                context.drawText(textRenderer, rightPageLines.get(i), bookX + RIGHT_PAGE_TEXT_X, drawYRight, 0x503000, false);
                drawYRight += LINE_HEIGHT;
            }

            for (ImageEntry img : rightPage.images()) {
                int x = bookX + RIGHT_PAGE_TEXT_X + img.x();
                int y = bookY + img.y();

                if (img.type() == ImageType.ITEM && img.itemStack() != null) {
                    renderScaledItem(context, img.itemStack(), x, y, img.width(), img.height());
                } else if (img.type() == ImageType.TEXTURE && img.identifier() != null) {
                    context.drawTexture(img.identifier(),
                            x, y,
                            0, 0,
                            img.width(), img.height(),
                            img.width(), img.height());
                }
                else if (img.type() == ImageType.CYCLING_ITEM && img.cyclingItemStacks() != null && !img.cyclingItemStacks().isEmpty()) {
                    int frameDuration = img.frameDurationTicks() > 0 ? img.frameDurationTicks() : DEFAULT_IMAGE_CYCLE_FRAME_DURATION_TICKS;
                    int currentFrameIndex = (globalImageCycleTick / frameDuration) % img.cyclingItemStacks().size();
                    ItemStack currentStack = img.cyclingItemStacks().get(currentFrameIndex);
                    renderScaledItem(context, currentStack, x, y, img.width(), img.height());
                }
                else if (img.type() == ImageType.CYCLING_TEXTURE && img.cyclingIdentifiers() != null && !img.cyclingIdentifiers().isEmpty()) {
                    int frameDuration = img.frameDurationTicks() > 0 ? img.frameDurationTicks() : DEFAULT_IMAGE_CYCLE_FRAME_DURATION_TICKS;
                    int currentFrameIndex = (globalImageCycleTick / frameDuration) % img.cyclingIdentifiers().size();
                    Identifier currentTexture = img.cyclingIdentifiers().get(currentFrameIndex);

                    context.drawTexture(currentTexture,
                            x, y,
                            0, 0,
                            img.width(), img.height(),
                            img.width(), img.height());
                }
            }
        }
    }

    private void renderScaledItem(DrawContext context, ItemStack stack, int x, int y, int width, int height) {
        if (width == 16 && height == 16) {
            context.drawItem(stack, x, y);
            return;
        }

        context.getMatrices().push();

        context.getMatrices().translate(x, y, 100);
        context.getMatrices().scale(width / 16.0f, height / 16.0f, 1.0f);

        context.drawItem(stack, 0, 0);

        context.getMatrices().pop();
    }

    class NavigationButton extends ButtonWidget {
        private static final int HOVER_OFFSET_Y = -2;
        private boolean wasHovered = false;

        protected NavigationButton(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.isHovered() && !wasHovered) {
                MinecraftClient.getInstance().getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance.master(ModSounds.GUIDE_BOOK_ENTRY, 1.0F, 0.5F));
            }
            wasHovered = this.isHovered();

            int drawY = this.getY();
            if (this.isHovered()) {
                drawY += HOVER_OFFSET_Y;
            }

            context.drawTexture(
                    ENTRY_BUTTON_TEXTURE,
                    this.getX(), drawY,
                    0, 0,
                    this.width, this.height,
                    100, 20
            );

            int textColor = 0x503000;
            context.drawText(textRenderer, this.getMessage(),
                    this.getX() + (this.width - textRenderer.getWidth(this.getMessage())) / 2,
                    drawY + (this.height - 8) / 2,
                    textColor, false);
        }
    }

    class BackButton extends ButtonWidget {

        protected BackButton(int x, int y, int width, int height, PressAction onPress) {
            super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

            Identifier identifier;
            if (this.isHovered()) {
                identifier = EXIT_PAGE_HIGHLIGHTED_TEXTURE;
            } else {
                identifier = EXIT_PAGE_TEXTURE;
            }

            context.drawTexture(identifier,
                    this.getX(), this.getY(),
                    0, 0,
                    18, 18,
                    18, 18);
        }
    }

    class BookmarkButton extends ButtonWidget {
        private final Category category;
        private boolean wasHovered = false;

        protected BookmarkButton(int x, int y, int width, int height, Text message, Category category, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.category = category;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.isHovered() && !wasHovered) {
                MinecraftClient.getInstance().getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance.master(ModSounds.GUIDE_BOOK_BOOKMARK, 1.0F, 1.0F));
            }
            wasHovered = this.isHovered();

            Identifier identifier = BOOKMARK_TEXTURE;
            int textColor = 0x000000;

            // Highlight text if hovered or if it's the currently active category
            if (this.isHovered() || GuideMainScreen.this.currentCategory == this.category) {
                identifier = BOOKMARK_HIGHLIGHTED_TEXTURE;
                textColor = 0xFFFFFF;
            }

            context.drawTexture(identifier,
                    this.getX(), this.getY(),
                    0, 0,
                    this.width, this.height,
                    this.width, this.height);

            int textX = this.getX() + BOOKMARK_TEXT_OFFSET_X;
            int textY = this.getY() + (this.height - textRenderer.fontHeight) / 2;
            context.drawText(textRenderer, this.getMessage(), textX, textY, textColor, false);
        }
    }
}