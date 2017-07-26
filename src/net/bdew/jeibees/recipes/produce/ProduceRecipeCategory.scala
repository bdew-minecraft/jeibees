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

package net.bdew.jeibees.recipes.produce

import forestry.api.genetics.ISpeciesRoot
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import net.bdew.jeibees.gui.Slot
import net.bdew.jeibees.misc.ItemStackDrawable
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class ProduceRecipeCategory(root: ISpeciesRoot, guiHelper: IGuiHelper, icon: ItemStack) extends IRecipeCategory[ProduceRecipe] {
  val background = guiHelper.createDrawable(new ResourceLocation("jeibees", "textures/recipes.png"), 0, 61, 162, 61)

  override def getUid = "bdew.jeibees.produce." + root.getUID
  override def getTitle = I18n.format(getUid)
  override def getBackground = background
  override def getModName: String = "Forestry"

  override lazy val getIcon = new ItemStackDrawable(icon)

  override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: ProduceRecipe, ingredients: IIngredients): Unit = {
    val itemStacks = recipeLayout.getItemStacks

    itemStacks.init(0, ProduceRecipeCategory.inputSlot.isInput, ProduceRecipeCategory.inputSlot.x, ProduceRecipeCategory.inputSlot.y)
    itemStacks.set(0, recipeWrapper.input)

    for ((((stack, chance), Slot(x, y, isInput)), slot) <- recipeWrapper.produceSanitized.take(3).zip(ProduceRecipeCategory.produceSlots).zipWithIndex) {
      itemStacks.init(slot + 1, isInput, x, y)
      itemStacks.set(slot + 1, stack)
    }

    for ((((stack, chance), Slot(x, y, isInput)), slot) <- recipeWrapper.specialtySanitized.take(3).zip(ProduceRecipeCategory.specialtySlots).zipWithIndex) {
      itemStacks.init(slot + 4, isInput, x, y)
      itemStacks.set(slot + 4, stack)
    }
  }
}

object ProduceRecipeCategory {
  val inputSlot = Slot(18, 15, true)
  val produceSlots = List(Slot(92, 4, false), Slot(114, 4, false), Slot(136, 4, false))
  val specialtySlots = List(Slot(92, 32, false), Slot(114, 32, false), Slot(136, 32, false))
}