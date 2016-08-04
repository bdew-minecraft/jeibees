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

package net.bdew.jeibees.misc

import net.bdew.jeibees.JEIBees
import net.minecraft.item.ItemStack

object ItemHelper {
  def isSameItem(s1: ItemStack, s2: ItemStack): Boolean = {
    if ((s1 == null) || (s2 == null)) return false
    if (s1.getItem ne s2.getItem) return false
    if (s1.getItemDamage != s2.getItemDamage) return false
    if ((s1.getTagCompound == null) && (s2.getTagCompound == null)) return true
    if ((s1.getTagCompound == null) || (s2.getTagCompound == null)) return false
    return s1.getTagCompound == s2.getTagCompound
  }

  def mergeStacks(stacks: Map[ItemStack, Float]): Map[ItemStack, Float] = {
    // There is probably a saner way to do this... but i can't think of it right now.
    var merged = Map.empty[ItemStack, Float]
    for ((stack, chance) <- stacks if stack != null && stack.getItem != null) {
      var added = false
      for ((mergedStack, mergedChance) <- merged if !added) {
        if (isSameItem(stack, mergedStack) && (chance == mergedChance)) {
          mergedStack.stackSize += stack.stackSize
          added = true
        }
      }
      if (!added)
        merged += stack -> chance
    }
    merged
  }

  def sanitizeDrops(drops: Map[ItemStack, Float], origin: String): Map[ItemStack, Float] = {
    if (drops == null) {
      JEIBees.logWarn("%s returned null", origin)
      Map.empty
    } else {
      val (bad, good) = drops.partition(x => x._1 == null || x._1.getItem == null)
      if (bad.nonEmpty)
        JEIBees.logWarn("%s contains nulls and/or corrupt item stacks", origin)
      good
    }
  }
}