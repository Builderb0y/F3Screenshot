package builderb0y.f3screenshot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import builderb0y.f3screenshot.mixins.DebugHudAccessor;

public class F3Screenshot implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("F3Screenshot");
	public static final String MISC = "Miscellaneous/un-sorted";

	@Override
	public void onInitializeClient() {
		System.setProperty("java.awt.headless", "false");
		LOGGER.info("Set java.awt.headless to false.");
		MixinEnvironment.getCurrentEnvironment().audit();
	}

	public static void saveF3(File screenshotsFolder, Consumer<Component> messageSender) {
		File f3File = getSaveFile(screenshotsFolder);
		Throwable saveException = saveF3Data(f3File);
		if (saveException == null) {
			Throwable copyException = copyFile(f3File);
			if (copyException == null) {
				messageSender.accept(
					Component
					.translatableWithFallback(
						"f3screenshot.f3.full_success",
						"Saved F3 data to %s and copied it to your clipboard.",
						f3File.getName()
					)
					.withStyle((Style style) -> style.withClickEvent(
						new ClickEvent.OpenFile(f3File)
					))
				);
			}
			else {
				messageSender.accept(
					Component
					.translatableWithFallback(
						"f3screenshot.f3.partial_success",
						"Saved F3 data to %s, but could not copy it to your clipboard: %s",
						f3File.getName(),
						copyException.getLocalizedMessage()
					)
					.withStyle((Style style) -> style.withClickEvent(
						new ClickEvent.OpenFile(f3File)
					))
				);
			}
		}
		else {
			messageSender.accept(Component.translatableWithFallback(
				"f3screenshot.f3.no_success",
				"Failed to save F3 data to file: %s",
				saveException
			));
		}
	}

	public static TreeMap<String, TreeMap<String, List<String>>> collectDebugInformation() {
		TreeMap<String, TreeMap<String, List<String>>> lineSections = new TreeMap<>();
		var debugHudLines = new DebugScreenDisplayer() {

			public String key;

			@Override
			public void addPriorityLine(String line) {
				lineSections.computeIfAbsent(MISC, (String $) -> new TreeMap<>()).computeIfAbsent(this.key, (String $) -> new ArrayList<>()).add(line);
			}

			@Override
			public void addLine(String line) {
				lineSections.computeIfAbsent(MISC, (String $) -> new TreeMap<>()).computeIfAbsent(this.key, (String $) -> new ArrayList<>()).add(line);
			}

			@Override
			public void addToGroup(Identifier sectionId, Collection<String> lines) {
				if (!lines.isEmpty()) {
					lineSections.computeIfAbsent(sectionId.toString(), (String $) -> new TreeMap<>()).computeIfAbsent(this.key, (String $) -> new ArrayList<>()).addAll(lines);
				}
			}

			@Override
			public void addToGroup(Identifier sectionId, String line) {
				lineSections.computeIfAbsent(sectionId.toString(), (String $) -> new TreeMap<>()).computeIfAbsent(this.key, (String $) -> new ArrayList<>()).add(line);
			}
		};
		DebugHudAccessor accessor = (DebugHudAccessor)(Minecraft.getInstance().getDebugOverlay());
		Level world = accessor.f3Screenshot_getWorld();
		LevelChunk clientChunk = accessor.f3Screenshot_getClientChunk();
		LevelChunk serverChunk = accessor.f3Screenshot_getServerChunk();
		for (Map.Entry<Identifier, DebugScreenEntry> entry : DebugScreenEntries.allEntries().entrySet()) {
			debugHudLines.key = entry.getKey().toString() + " (" + entry.getValue().getClass() + ')';
			entry.getValue().display(debugHudLines, world, clientChunk, serverChunk);
		}
		return lineSections;
	}

	public static Throwable saveF3Data(File f3File) {
		TreeMap<String, TreeMap<String, List<String>>> sections = collectDebugInformation();
		try (BufferedWriter writer = Files.newBufferedWriter(f3File.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
			for (Map.Entry<String, TreeMap<String, List<String>>> outer : sections.entrySet()) {
				writer.append(outer.getKey()).append(':').append('\n');
				for (Map.Entry<String, List<String>> inner : outer.getValue().entrySet()) {
					writer.append('\t').append(inner.getKey()).append(':').append('\n');
					for (String line : inner.getValue()) {
						writer.append('\t').append('\t').append(line).append('\n');
					}
				}
			}
			return null;
		}
		catch (Exception exception) {
			LOGGER.error("Failed to save F3 data to file:", exception);
			return exception;
		}
	}

	public static File getSaveFile(File screenshotsFolder) {
		String time = Util.getFilenameFormattedDateTime();
		File file = new File(screenshotsFolder, time + "_F3.txt");
		if (!file.exists()) return file;

		for (int attempt = 2; true; attempt++) {
			file = new File(screenshotsFolder, time + '_' + attempt + "_F3.txt");
			if (!file.exists()) return file;
		}
	}

	public static Throwable copyFile(File file) {
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new Transferable() {

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.javaFileListFlavor };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor flavor) {
						return flavor.equals(DataFlavor.javaFileListFlavor);
					}

					@Override
					public @NotNull Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
						if (flavor.equals(DataFlavor.javaFileListFlavor)) {
							return Collections.singletonList(file);
						}
						else {
							throw new UnsupportedFlavorException(flavor);
						}
					}
				},
				(Clipboard clipboard, Transferable contents) -> {}
			);
			return null;
		}
		catch (Exception exception) {
			F3Screenshot.LOGGER.error("Exception copying F3 data to clipboard.", exception);
			return exception;
		}
	}
}