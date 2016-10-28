package io.gitlab.arturbosch.tinbo

import io.gitlab.arturbosch.tinbo.plugins.TiNBoPlugin
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.shell.core.annotation.CliOption
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

/**
 * @author Artur Bosch
 */
class Ascii : TiNBoPlugin {

	@CliCommand("plugin ascii", help = "")
	fun run(@CliOption(key = arrayOf("")) path: String?): String {
		return path?.let {
			val path1 = Paths.get(path)
			if (Files.notExists(path1)) return "Specified path does not exist!"
			if (!isImage(path1)) return "Given path points not to an image (jpg or png)."
			val image = ImageIO.read(path1.toFile())
			val resizeImage = resizeImage(image, 76, 0)
			return ASCII().convert(resizeImage)
		} ?: "Provided path does not exist"
	}

	private fun isImage(path: Path): Boolean {
		val stringPath = path.toString().toLowerCase()
		fun endsWith(sub: String): Boolean = stringPath.endsWith(sub)
		return endsWith("jpg") || endsWith("jpeg") || endsWith("png")
	}

	private fun resizeImage(image: BufferedImage, width: Int, height: Int): BufferedImage {
		var cWidth = width
		var cHeight = height
		if (cWidth < 1) {
			cWidth = 1
		}
		if (cHeight <= 0) {
			val aspectRatio = cWidth.toDouble() / image.width * 0.5
			cHeight = Math.ceil(image.height * aspectRatio).toInt()
		}
		val resized = BufferedImage(cWidth, cHeight, BufferedImage.TYPE_INT_RGB)
		val scaled = image.getScaledInstance(cWidth, cHeight, Image.SCALE_DEFAULT)
		resized.graphics.drawImage(scaled, 0, 0, null)
		return resized
	}

	class ASCII(val negative: Boolean = false) {

		fun convert(image: BufferedImage): String {
			val sb = StringBuilder((image.width + 1) * image.height)
			for (y in 0..image.height - 1) {
				if (sb.length != 0) sb.append("\n")
				for (x in 0..image.width - 1) {
					val pixelColor = Color(image.getRGB(x, y))
					val gValue = pixelColor.red.toDouble() * 0.30 +
							pixelColor.blue.toDouble() * 0.59 +
							pixelColor.green.toDouble() * 0.11
					val s = if (negative) returnStrNeg(gValue) else returnStrPos(gValue)
					sb.append(s)
				}
			}
			return sb.toString()
		}

		private fun returnStrPos(g: Double) = when {
			g >= 230.0 -> ' '
			g >= 200.0 -> '.'
			g >= 180.0 -> '*'
			g >= 160.0 -> ':'
			g >= 130.0 -> 'o'
			g >= 100.0 -> '&'
			g >= 70.0 -> '8'
			g >= 50.0 -> '#'
			else -> '@'
		}

		private fun returnStrNeg(g: Double) = when {
			g >= 230.0 -> '@'
			g >= 200.0 -> '#'
			g >= 180.0 -> '8'
			g >= 160.0 -> '&'
			g >= 130.0 -> 'o'
			g >= 100.0 -> ':'
			g >= 70.0 -> '*'
			g >= 50.0 -> '.'
			else -> ' '
		}
	}
}