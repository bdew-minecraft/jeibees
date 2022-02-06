package net.bdew.jeibees.gui

import com.mojang.blaze3d.matrix.MatrixStack
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.ingredients.IIngredientRenderer
import net.bdew.jeibees.BeesJEIPlugin
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent

import java.util

class StackWithChanceRenderer(map: Map[ItemStack, Float]) extends IIngredientRenderer[ItemStack] {
  private val subRenderer = BeesJEIPlugin.ingredientHelper.getIngredientRenderer(VanillaTypes.ITEM)

  override def render(matrixStack: MatrixStack, xPosition: Int, yPosition: Int, ingredient: ItemStack): Unit = {
    subRenderer.render(matrixStack, xPosition, yPosition, ingredient)
    TextRender.drawCentered(matrixStack, "%.0f%%".format(map(ingredient) * 100F), xPosition + 8, yPosition + 18, 0xFFFFFFFF)
  }

  override def getTooltip(ingredient: ItemStack, tooltipFlag: ITooltipFlag): util.List[ITextComponent] =
    subRenderer.getTooltip(ingredient, tooltipFlag)
}
