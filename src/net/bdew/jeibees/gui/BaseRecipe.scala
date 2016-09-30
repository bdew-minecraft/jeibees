/*
 * Copyright (c) bdew, 2016
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

import java.util

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.BlankRecipeWrapper
import net.bdew.jeibees.misc.ExtendedIngredients
import net.minecraft.client.Minecraft

import scala.collection.JavaConversions._

abstract class BaseRecipe extends BlankRecipeWrapper {
  private var widgets = List.empty[RecipeWidget]

  def addWidget(widget: RecipeWidget) = widgets :+= widget
  def addWidgets(toAdd: Iterable[RecipeWidget]) = widgets ++= toAdd

  private def inRect(widget: RecipeWidget, mouseX: Int, mouseY: Int) =
    mouseX >= widget.x && mouseX <= widget.x + widget.w && mouseY >= widget.y && mouseY <= widget.y + widget.h

  override def handleClick(minecraft: Minecraft, mouseX: Int, mouseY: Int, mouseButton: Int): Boolean = {
    widgets.exists(w => inRect(w, mouseX, mouseY) && w.clicked(mouseX - w.x, mouseY - w.y))
  }

  override def getTooltipStrings(mouseX: Int, mouseY: Int): util.List[String] = {
    widgets.filter(inRect(_, mouseX, mouseY)).flatMap(w => w.getTooltip(mouseX - w.x, mouseY - w.y))
  }

  override def drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int): Unit = {
    widgets.foreach(w => w.draw(mouseX - w.x, mouseY - w.y))
  }

  def setIngredientsExtended(ingredients: ExtendedIngredients): Unit

  override final def getIngredients(ingredients: IIngredients): Unit = setIngredientsExtended(new ExtendedIngredients(ingredients))
}
