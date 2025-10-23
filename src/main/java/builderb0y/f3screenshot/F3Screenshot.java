package builderb0y.f3screenshot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import builderb0y.f3screenshot.mixins.DebugHudAccessor;

public class F3Screenshot implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("F3Screenshot");
	public static final String MISC = "Miscellaneous/un-sorted";

	@Override
	public void onInitializeClient() {
		System.setProperty("java.awt.headless", "false");
		LOGGER.info("Set java.awt.headless to false.");
	}

	public static void saveF3(File screenshotsFolder, Consumer<Text> messageSender) {
		TreeMap<String, TreeMap<String, List<String>>> sections = collectDebugInformation();
		File f3File = getSaveFile(screenshotsFolder);
		Throwable saveException = saveF3Data(f3File, sections);
		if (saveException == null) {
			Throwable copyException = copyFile(f3File);
			if (copyException == null) {
				messageSender.accept(
					Text
					.translatableWithFallback(
						"f3screenshot.full_success",
						"Saved F3 data to %s and copied it to your clipboard.",
						f3File.getName()
					)
					.styled((Style style) -> style.withClickEvent(new ClickEvent.OpenFile(f3File)))
				);
			}
			else {
				messageSender.accept(
					Text
					.translatableWithFallback(
						"f3screenshot.partial_success",
						"Saved F3 data to %s, but could not copy it to your clipboard: %s",
						f3File.getName(),
						copyException.getLocalizedMessage()
					)
					.styled((Style style) -> style.withClickEvent(new ClickEvent.OpenFile(f3File)))
				);
			}
		}
		else {
			messageSender.accept(Text.translatable(
				"f3screenshot.no_success",
				"Failed to save F3 data to file: %s",
				saveException
			));
		}
	}

	public static TreeMap<String, TreeMap<String, List<String>>> collectDebugInformation() {
		TreeMap<String, TreeMap<String, List<String>>> lineSections = new TreeMap<>();
		var debugHudLines = new DebugHudLines() {

			public String key;

			@Override
			public void addPriorityLine(String line) {
				lineSections.computeIfAbsent(MISC, $ -> new TreeMap<>()).computeIfAbsent(this.key, $ -> new ArrayList<>()).add(line);
			}

			@Override
			public void addLine(String line) {
				lineSections.computeIfAbsent(MISC, $ -> new TreeMap<>()).computeIfAbsent(this.key, $ -> new ArrayList<>()).add(line);
			}

			@Override
			public void addLinesToSection(Identifier sectionId, Collection<String> lines) {
				if (!lines.isEmpty()) {
					lineSections.computeIfAbsent(sectionId.toString(), $ -> new TreeMap<>()).computeIfAbsent(this.key, $ -> new ArrayList<>()).addAll(lines);
				}
			}

			@Override
			public void addLineToSection(Identifier sectionId, String line) {
				lineSections.computeIfAbsent(sectionId.toString(), $ -> new TreeMap<>()).computeIfAbsent(this.key, $ -> new ArrayList<>()).add(line);
			}
		};
		DebugHudAccessor accessor = (DebugHudAccessor)(MinecraftClient.getInstance().getDebugHud());
		World world = accessor.f3Screenshot_getWorld();
		WorldChunk clientChunk = accessor.f3Screenshot_getClientChunk();
		WorldChunk serverChunk = accessor.f3Screenshot_getServerChunk();
		for (Map.Entry<Identifier, DebugHudEntry> entry : DebugHudEntries.getEntries().entrySet()) {
			debugHudLines.key = entry.getKey().toString() + " (" + entry.getValue().getClass() + ')';
			entry.getValue().render(debugHudLines, world, clientChunk, serverChunk);
		}
		return lineSections;
	}

	public static File getSaveFile(File screenshotsFolder) {
		String time = Util.getFormattedCurrentTime();
		File file = new File(screenshotsFolder, time + "_F3.txt");
		if (!file.exists()) return file;

		for (int attempt = 2; true; attempt++) {
			file = new File(screenshotsFolder, time + '_' + attempt + "_F3.txt");
			if (!file.exists()) return file;
		}
	}

	public static Throwable saveF3Data(File f3File, TreeMap<String, TreeMap<String, List<String>>> sections) {
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