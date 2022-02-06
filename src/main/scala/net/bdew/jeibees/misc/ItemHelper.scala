package net.bdew.jeibees.misc

import net.bdew.jeibees.JEIBees
import net.minecraft.item.ItemStack

import java.util.Collections
import scala.jdk.CollectionConverters._

object ItemHelper {
  def isSameItem(s1: ItemStack, s2: ItemStack): Boolean = {
    if (s1.isEmpty || s2.isEmpty) return false
    if (s1.getItem != s2.getItem) return false
    if (s1.getDamageValue != s2.getDamageValue) return false
    if ((s1.getTag == null) && (s2.getTag == null)) return true
    if ((s1.getTag == null) || (s2.getTag == null)) return false
    return s1.getTag == s2.getTag
  }

  def mergeStacks(stacks: Map[ItemStack, Float]): Map[ItemStack, Float] = {
    // There is probably a saner way to do this... but i can't think of it right now.
    var merged = Map.empty[ItemStack, Float]
    for ((stack, chance) <- stacks if !stack.isEmpty) {
      var added = false
      for ((mergedStack, mergedChance) <- merged if !added) {
        if (isSameItem(stack, mergedStack) && (chance == mergedChance)) {
          mergedStack.grow(stack.getCount)
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
      drops.filter(x => !x._1.isEmpty)
    }
  }

  def outputsList(drops: List[(ItemStack, Float)], slots: Int): List[java.util.List[ItemStack]] = {
    if (drops.size <= slots) return drops.map(x => Collections.singletonList(x._1))
    val grouped = drops.groupBy(_._2).toList.sortBy(-_._1)
    if (grouped.size <= slots) {
      return grouped.map(_._2.map(_._1).asJava)
    }
    val first = grouped.take(slots - 1).map(_._2.map(_._1).asJava)
    val rest = grouped.drop(slots - 1).flatMap(_._2).map(_._1).asJava
    first :+ rest
  }
}