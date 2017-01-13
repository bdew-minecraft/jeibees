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

package net.bdew.jeibees.misc

import mezz.jei.api.gui.IDrawable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraft.item.ItemStack

class ItemStackDrawable(item: ItemStack) extends IDrawable {
  override def getWidth: Int = 16
  override def getHeight: Int = 16

  override def draw(minecraft: Minecraft): Unit = draw(minecraft, 0, 0)

  override def draw(minecraft: Minecraft, xOffset: Int, yOffset: Int): Unit = {
    GlStateManager.enableDepth()
    RenderHelper.enableGUIStandardItemLighting()
    minecraft.getRenderItem.renderItemAndEffectIntoGUI(null, item, xOffset, yOffset)
    RenderHelper.disableStandardItemLighting()
    GlStateManager.disableDepth()
  }
}
