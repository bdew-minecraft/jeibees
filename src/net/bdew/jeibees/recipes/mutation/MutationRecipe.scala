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

package net.bdew.jeibees.recipes.mutation

import com.mojang.realmsclient.gui.ChatFormatting
import forestry.api.genetics.IMutation
import net.bdew.jeibees.gui.{BaseRecipe, LabelCentered, LabelHelper}
import net.bdew.jeibees.misc.GeneticsHelper
import net.bdew.jeibees.{Config, JEIBees}

import scala.collection.JavaConversions._

class MutationRecipe(val mutation: IMutation, val category: MutationRecipeCategory) extends BaseRecipe {
  val p1stack = GeneticsHelper.getItemFromSpecies(mutation.getAllele0, GeneticsHelper.Position.P1)
  val p2stack = GeneticsHelper.getItemFromSpecies(mutation.getAllele1, GeneticsHelper.Position.P2)
  val resStack = GeneticsHelper.getItemFromTemplate(mutation.getRoot, mutation.getTemplate, GeneticsHelper.Position.RES)
  val resSpecies = mutation.getTemplate()(mutation.getRoot.getSpeciesChromosomeType.ordinal())

  val requirements = try {
    Option(mutation.getSpecialConditions.toList).getOrElse(List.empty)
  } catch {
    case e: Throwable =>
      JEIBees.logErrorException("Exception in getSpecialConditions for mutation %s + %s", e, mutation.getAllele0.getUID, mutation.getAllele1.getUID)
      List(ChatFormatting.RED + "[ERROR!]")
  }

  addWidgets(LabelHelper.multilineCentered(category.slots(0).x + 9, category.slots(0).y + 22, LabelHelper.splitIfNeeded(mutation.getAllele0.getName, " "), 0xFFFFFFFF))
  addWidgets(LabelHelper.multilineCentered(category.slots(1).x + 9, category.slots(1).y + 22, LabelHelper.splitIfNeeded(mutation.getAllele1.getName, " "), 0xFFFFFFFF))
  addWidgets(LabelHelper.multilineCentered(category.slots(2).x + 9, category.slots(2).y + 22, LabelHelper.splitIfNeeded(resSpecies.getName, " "), 0xFFFFFFFF))

  if (requirements.isEmpty || !Config.showRequirements) {
    addWidget(new LabelCentered(105, 12, "%.0f%%".format(mutation.getBaseChance), 0xFFFFFF))
  } else {
    addWidget(new LabelCentered(105, 12, "[%.0f%%]".format(mutation.getBaseChance), 0xFFFFFF, toolTip = requirements))
  }

  override def getInputs = GeneticsHelper.getAllItemsFromSpecies(mutation.getAllele0) ++ GeneticsHelper.getAllItemsFromSpecies(mutation.getAllele1)
  override def getOutputs = GeneticsHelper.getAllItemsFromTemplate(mutation.getRoot, mutation.getTemplate)
}
