package net.bdew.jeibees.misc

import forestry.api.apiculture.genetics.{EnumBeeType, IAlleleBeeSpecies, IBeeRoot}
import forestry.api.arboriculture.genetics.{EnumGermlingType, IAlleleTreeSpecies, ITreeRoot}
import forestry.api.genetics.IForestrySpeciesRoot
import forestry.api.genetics.products.IProductList
import forestry.api.lepidopterology.genetics.{EnumFlutterType, IButterflyRoot}
import genetics.api.GeneticsAPI
import genetics.api.alleles.{IAllele, IAlleleSpecies}
import genetics.api.individual.IIndividual
import genetics.api.organism.IOrganismType
import genetics.api.root.IIndividualRoot
import net.bdew.jeibees.Config
import net.minecraft.item.ItemStack

import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

object GeneticsHelper {

  object Position extends Enumeration {
    val P1, P2, RES = Value
  }

  def activeRoots: Seq[IIndividualRoot[_]] =
    GeneticsAPI.apiInstance.getRoots.asScala
      .filter(_._2.isPresent)
      .map(x => x._2.get()).toList

  def getSpeciesTypeForPosition(root: IIndividualRoot[_], position: Position.Value): IOrganismType = {
    root match {
      case x: IForestrySpeciesRoot[_] => x.getTypeForMutation(position.id)
      case _ => root.getTypes.getDefaultType
    }
  }

  def getItemFromTemplate[I <: IIndividual](root: IIndividualRoot[I], tpl: Array[IAllele], position: Position.Value): ItemStack = {
    val individual = root.templateAsIndividual(tpl)
    if (Config.identifyGenome.get())
      individual.analyze()
    root.getTypes.getHandler(getSpeciesTypeForPosition(root, position)).get().createStack(individual)
  }

  def getItemFromSpecies(species: IAlleleSpecies, position: Position.Value): ItemStack = {
    val root = species.getRoot
    val tpl = root.getTemplate(species.getRegistryName.toString)
    getItemFromTemplate(root, tpl, position)
  }

  def getAllSpeciesTypes[I <: IIndividual](root: IIndividualRoot[I]): List[IOrganismType] = {
    root match {
      case x: IBeeRoot => EnumBeeType.VALUES.toList
      case x: ITreeRoot => EnumGermlingType.VALUES.toList
      case x: IButterflyRoot => EnumFlutterType.VALUES.toList
      case x: IIndividualRoot[I] => root.getTypes.getTypes.asScala.toList
      case _ => List.empty
    }
  }

  def getAllItemsFromTemplate[I <: IIndividual](root: IIndividualRoot[I], tpl: Array[IAllele]): List[ItemStack] = {
    val individual = root.templateAsIndividual(tpl)

    if (Config.identifyGenome.get())
      individual.analyze()

    getAllSpeciesTypes(root) flatMap { organism =>
      root.getTypes.getHandler(organism).toScala map { handler =>
        handler.createStack(individual)
      }
    }
  }

  def getAllItemsFromSpecies(species: IAlleleSpecies): List[ItemStack] = {
    val root = species.getRoot
    val tpl = root.getTemplate(species.getRegistryName.toString)
    getAllItemsFromTemplate(root, tpl)
  }

  def productsToMap(products: IProductList): Map[ItemStack, Float] = {
    products.getPossibleProducts.asScala.map(x => x.getStack -> x.getChance).toMap
  }

  def getProduceAndSpecialty(species: IAlleleSpecies): (Map[ItemStack, Float], Map[ItemStack, Float]) = {
    species match {
      case bee: IAlleleBeeSpecies =>
        (productsToMap(bee.getProducts), productsToMap(bee.getSpecialties))
      case tree: IAlleleTreeSpecies =>
        val individual = individualFromSpeciesTemplate(tree.getRoot, tree)
        //todo: Get log block through IWoodType
        (productsToMap(individual.getProducts), productsToMap(individual.getSpecialties))
      case _ => (Map.empty, Map.empty)
    }
  }

  def individualFromSpeciesTemplate[I <: IIndividual](root: IIndividualRoot[I], species: IAlleleSpecies): I = {
    val res = root.templateAsIndividual(root.getTemplate(species.getRegistryName.toString))
    if (Config.identifyGenome.get())
      res.analyze()
    res
  }

  def getDefaultMemberStack[I <: IIndividual](root: IIndividualRoot[I]): ItemStack = {
    root.createStack(root.getDefaultMember, root.getTypes.getDefaultType)
  }

  def getProducerStack(species: IAlleleSpecies): ItemStack = {
    species.getRoot match {
      case bees: IBeeRoot => bees.createStack(individualFromSpeciesTemplate(bees, species), EnumBeeType.QUEEN)
      case trees: ITreeRoot => trees.createStack(individualFromSpeciesTemplate(trees, species), EnumGermlingType.SAPLING)
      case root: IIndividualRoot[x] => root.createStack(individualFromSpeciesTemplate[x](root, species), root.getTypes.getDefaultType)
    }
  }
}
