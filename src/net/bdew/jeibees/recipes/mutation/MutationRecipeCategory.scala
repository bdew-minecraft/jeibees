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

package net.bdew.jeibees.recipes.mutation

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

class MutationRecipeCategory(root: ISpeciesRoot, guiHelper: IGuiHelper, icon: ItemStack) extends IRecipeCategory[MutationRecipe] {
  val background = guiHelper.createDrawable(new ResourceLocation("jeibees", "textures/recipes.png"), 0, 0, 162, 61)
  override def getUid = "bdew.jeibees.mutation." + root.getUID
  override def getTitle = I18n.format(getUid)
  override def getBackground = background
  override def getModName: String = "Forestry"

  override lazy val getIcon = new ItemStackDrawable(icon)

  override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: MutationRecipe, ingredients: IIngredients): Unit = {
    val itemStacks = recipeLayout.getItemStacks
    MutationRecipeCategory.slots foreach { case (slot, Slot(x, y, isInput)) =>
      itemStacks.init(slot, isInput, x, y)
    }
    itemStacks.set(0, recipeWrapper.p1stack)
    itemStacks.set(1, recipeWrapper.p2stack)
    itemStacks.set(2, recipeWrapper.resStack)
  }
}

object MutationRecipeCategory {
  val slots = Map(
    0 -> Slot(18, 15, true),
    1 -> Slot(71, 15, true),
    2 -> Slot(125, 15, false)
  )
}