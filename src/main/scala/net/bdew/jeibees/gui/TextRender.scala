package net.bdew.jeibees.gui

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.util.text.ITextComponent

object TextRender {
  val font: FontRenderer = Minecraft.getInstance().font

  def splitIfNeeded(string: String, separator: String): List[String] = {
    if (string.contains(separator))
      string.split(separator).toList
    else
      List(string)
  }

  def drawCentered(ps: MatrixStack, text: String, x: Float, y: Float, color: Int): Unit = {
    val w = font.width(text)
    font.draw(ps, text, x - (w / 2), y, color)
  }

  def drawCentered(ps: MatrixStack, text: ITextComponent, x: Float, y: Float, color: Int): Unit = {
    val w = font.width(text)
    font.draw(ps, text, x - (w / 2), y, color)
  }

  def drawCenteredMulti(ps: MatrixStack, text: String, x: Float, y: Float, color: Int): Unit = {
    val split = splitIfNeeded(text, " ")
    for ((line, i) <- split.zipWithIndex)
      drawCentered(ps, line, x, y + font.lineHeight * i, color)
  }

  def drawCenteredMulti(ps: MatrixStack, text: ITextComponent, x: Float, y: Float, color: Int): Unit = {
    drawCenteredMulti(ps, text.getString, x, y, color)
  }
}
