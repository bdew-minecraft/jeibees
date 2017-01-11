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

import mezz.jei.api.recipe.{IRecipeHandler, IRecipeWrapper}

object ProduceRecipeHandler extends IRecipeHandler[ProduceRecipe] {
  override def getRecipeWrapper(recipe: ProduceRecipe): IRecipeWrapper = recipe
  override def getRecipeCategoryUid(recipe: ProduceRecipe): String = recipe.category.getUid
  override def isRecipeValid(recipe: ProduceRecipe): Boolean = true
  override def getRecipeClass: Class[ProduceRecipe] = classOf[ProduceRecipe]
}
