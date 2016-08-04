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

import forestry.api.apiculture.{EnumBeeType, IAlleleBeeSpecies, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, IAlleleTreeSpecies, ITreeRoot}
import forestry.api.genetics.{IAllele, IAlleleSpecies, ISpeciesRoot, ISpeciesType}
import forestry.api.lepidopterology.{EnumFlutterType, IButterflyRoot}
import net.bdew.jeibees.Config
import net.minecraft.item.ItemStack

import scala.collection.JavaConversions._

object GeneticsHelper {

  object Position extends Enumeration {
    val P1, P2, RES = Value
  }

  def getSpeciesTypeForPosition(root: ISpeciesRoot, position: Position.Value): ISpeciesType = {
    root match {
      case x: IBeeRoot =>
        position match {
          case Position.P1 => EnumBeeType.PRINCESS
          case Position.P2 => EnumBeeType.DRONE
          case Position.RES => EnumBeeType.QUEEN
        }
      case x: ITreeRoot => EnumGermlingType.SAPLING
      case x: IButterflyRoot => EnumFlutterType.BUTTERFLY
      case _ => root.getIconType
    }
  }

  def getItemFromTemplate(root: ISpeciesRoot, tpl: Array[IAllele], position: Position.Value): ItemStack = {
    val individual = root.templateAsIndividual(tpl)
    if (Config.identifyGenome)
      individual.analyze()
    root.getMemberStack(individual, getSpeciesTypeForPosition(root, position))
  }

  def getItemFromSpecies(species: IAlleleSpecies, position: Position.Value): ItemStack = {
    val root = species.getRoot
    val tpl = root.getTemplate(species.getUID)
    getItemFromTemplate(root, tpl, position)
  }

  def getAllSpeciesTypes(root: ISpeciesRoot): List[ISpeciesType] = {
    root match {
      case x: IBeeRoot => EnumBeeType.VALUES.toList
      case x: ITreeRoot => EnumGermlingType.VALUES.toList
      case x: IButterflyRoot => EnumFlutterType.VALUES.toList
      case _ => List(root.getIconType)
    }
  }

  def getAllItemsFromTemplate(root: ISpeciesRoot, tpl: Array[IAllele]): List[ItemStack] = {
    val individual = root.templateAsIndividual(tpl)
    if (Config.identifyGenome)
      individual.analyze()
    getAllSpeciesTypes(root).map(t => root.getMemberStack(individual, t))
  }

  def getAllItemsFromSpecies(species: IAlleleSpecies): List[ItemStack] = {
    val root = species.getRoot
    val tpl = root.getTemplate(species.getUID)
    getAllItemsFromTemplate(root, tpl)
  }

  def fixMap(j: java.util.Map[ItemStack, java.lang.Float]): Map[ItemStack, Float] = j.mapValues(Float.unbox).toMap

  def getProduceAndSpecialty(species: IAlleleSpecies): (Map[ItemStack, Float], Map[ItemStack, Float]) = {
    species match {
      case bee: IAlleleBeeSpecies =>
        (fixMap(bee.getProductChances), fixMap(bee.getSpecialtyChances))
      case tree: IAlleleTreeSpecies =>
        val root = tree.getRoot
        val template = root.getTemplate(tree.getUID)
        val individual = root.templateAsIndividual(template)
        (Map(tree.getWoodProvider.getWoodStack -> 1F) ++ fixMap(individual.getProducts), fixMap(individual.getSpecialties))
      case _ => (Map.empty, Map.empty)
    }
  }

}
