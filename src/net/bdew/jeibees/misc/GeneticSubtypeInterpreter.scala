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

import forestry.api.genetics.AlleleManager
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter
import net.bdew.jeibees.JEIBees
import net.minecraft.item.ItemStack

object GeneticSubtypeInterpreter extends ISubtypeInterpreter {
  override def getSubtypeInfo(itemStack: ItemStack): String = {
    if (itemStack.hasTagCompound) {
      try {
        val individual = AlleleManager.alleleRegistry.getIndividual(itemStack)
        if (individual == null)
          null
        else
          individual.getGenome.getPrimary.getUID
      } catch {
        case e: Throwable =>
          JEIBees.logWarnException("Error getting species UID for %s (%s)", e, itemStack.toString, itemStack.getTagCompound.toString)
          null
      }
    } else ""
  }
}
