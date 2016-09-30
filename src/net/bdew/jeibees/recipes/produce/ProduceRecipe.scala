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

package net.bdew.jeibees.recipes.produce

import forestry.api.genetics.IAlleleSpecies
import net.bdew.jeibees.gui.{BaseRecipe, LabelCentered, LabelHelper}
import net.bdew.jeibees.misc.{ExtendedIngredients, GeneticsHelper, ItemHelper}

class ProduceRecipe(val species: IAlleleSpecies, val category: ProduceRecipeCategory) extends BaseRecipe {
  val (produce, specialty) = GeneticsHelper.getProduceAndSpecialty(species)
  val produceSanitized = ItemHelper.mergeStacks(ItemHelper.sanitizeDrops(produce, species.getUID + " drops")).toList.sortBy(-_._2)
  val specialtySanitized = ItemHelper.mergeStacks(ItemHelper.sanitizeDrops(specialty, species.getUID + " specialty")).toList.sortBy(-_._2)

  def hasProducts = produce.nonEmpty || specialty.nonEmpty

  val input = GeneticsHelper.getItemFromSpecies(species, GeneticsHelper.Position.P1)

  addWidgets(LabelHelper.multilineCentered(category.inputSlot.x + 9, category.inputSlot.y + 22, LabelHelper.splitIfNeeded(species.getName, " "), 0xFFFFFFFF))

  for (((stack, chance), slot) <- produceSanitized.take(3).zip(category.produceSlots)) {
    addWidget(new LabelCentered(slot.x + 9, slot.y + 19, "%.0f%%".format(chance * 100F), 0xFFFFFFFF))
  }

  for (((stack, chance), slot) <- specialtySanitized.take(3).zip(category.specialtySlots)) {
    addWidget(new LabelCentered(slot.x + 9, slot.y + 19, "%.0f%%".format(chance * 100F), 0xFFFFFFFF))
  }

  override def setIngredientsExtended(ingredients: ExtendedIngredients): Unit = {
    ingredients.setInputLists(List(GeneticsHelper.getAllItemsFromSpecies(species)))
    ingredients.setOutputs(produceSanitized.map(_._1) ++ specialtySanitized.map(_._1))
  }
}
