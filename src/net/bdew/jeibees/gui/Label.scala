/*
 * Copyright (c) bdew, 2016 - 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bdew.jeibees.gui

import net.minecraft.client.Minecraft

class Label(x: Int, y: Int, text: String, color: Int, shadow: Boolean = false, toolTip: List[String] = List.empty) extends RecipeWidget(x, y, LabelHelper.fontRenderer.getStringWidth(text), LabelHelper.fontRenderer.FONT_HEIGHT) {
  override def draw(mx: Int, my: Int): Unit = {
    LabelHelper.fontRenderer.drawString(text, x, y, color, shadow)
  }
  override def clicked(mx: Int, my: Int): Boolean = false
  override def getTooltip(mx: Int, my: Int): List[String] = toolTip
}

class LabelCentered(x: Int, y: Int, text: String, color: Int, shadow: Boolean = false, toolTip: List[String] = List.empty)
  extends Label(x - (LabelHelper.fontRenderer.getStringWidth(text) / 2), y, text, color, shadow, toolTip)

object LabelHelper {
  lazy val fontRenderer = Minecraft.getMinecraft.fontRenderer

  def splitIfNeeded(string: String, separator: String) = {
    if (string.contains(separator))
      string.split(separator).toList
    else
      List(string)
  }

  def multiline(x: Int, y: Int, text: List[String], color: Int, shadow: Boolean = false, toolTip: List[String] = List.empty) = {
    for ((line, i) <- text.zipWithIndex)
      yield new Label(x, y + fontRenderer.FONT_HEIGHT * i, line, color, shadow, toolTip)
  }

  def multilineCentered(x: Int, y: Int, text: List[String], color: Int, shadow: Boolean = false, toolTip: List[String] = List.empty) = {
    for ((line, i) <- text.zipWithIndex)
      yield new LabelCentered(x, y + fontRenderer.FONT_HEIGHT * i, line, color, shadow, toolTip)
  }
}