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

package net.bdew.jeibees

import forestry.api.apiculture.IBeeRoot
import forestry.api.arboriculture.ITreeRoot
import forestry.api.genetics.AlleleManager
import mezz.jei.api._
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.bdew.jeibees.misc.GeneticSubtypeInterpreter
import net.bdew.jeibees.recipes.mutation.{MutationRecipe, MutationRecipeCategory}
import net.bdew.jeibees.recipes.produce.{ProduceRecipe, ProduceRecipeCategory}
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.ResourceLocation

import scala.collection.JavaConversions._

@JEIPlugin
class BeesJEIPlugin extends IModPlugin {
  val geneticsItems = List(
    new ResourceLocation("forestry", "bee_larvae_ge"),
    new ResourceLocation("forestry", "sapling"),
    new ResourceLocation("forestry", "pollen_fertile"),
    new ResourceLocation("forestry", "butterfly_ge"),
    new ResourceLocation("forestry", "caterpillar_ge"),
    new ResourceLocation("forestry", "cocoon"),
    new ResourceLocation("forestry", "serum_ge")
  )

  override def registerItemSubtypes(subtypeRegistry: ISubtypeRegistry): Unit = {
    for (id <- geneticsItems; item <- Option(Item.REGISTRY.getObject(id))) {
      subtypeRegistry.registerSubtypeInterpreter(item, GeneticSubtypeInterpreter)
      JEIBees.logInfo("Added subtype interpreter for: %s (%s)", id, item.getUnlocalizedName)
    }
  }

  lazy val configs = {
    for (root <- AlleleManager.alleleRegistry.getSpeciesRoot.values())
      yield root -> Config.rootConfigs.getOrElse(root, Config.disabledConfig)
  }.toMap

  override def registerCategories(registry: IRecipeCategoryRegistration): Unit = {
    val guiHelper = registry.getJeiHelpers.getGuiHelper
    for ((root, cfg) <- configs) {
      if (cfg.showMutations) {
        val defaultIndividual = root.templateAsIndividual(root.getDefaultTemplate)
        registry.addRecipeCategories(new MutationRecipeCategory(root, guiHelper, root.getMemberStack(defaultIndividual, root.getIconType)))
      }
      if (cfg.showProduce) {
        val produceItem = root match {
          case _: IBeeRoot => new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("forestry", "bee_combs")))
          case _: ITreeRoot => new ItemStack(Items.APPLE)
          case _ => new ItemStack(Items.BUCKET) // Fallback for unknown types
        }
        registry.addRecipeCategories(new ProduceRecipeCategory(root, guiHelper, produceItem))
      }
    }
  }

  override def register(registry: IModRegistry): Unit = {
    JEIBees.logInfo("JEI Plugin initializing")

    for ((root, cfg) <- configs) {
      if (cfg.showMutations) {
        var mutations = root.getMutations(false)
        if (!Config.showSecret)
          mutations = mutations.filterNot(_.isSecret)
        val recipes = mutations.map(new MutationRecipe(_)).toList
        registry.addRecipes(recipes, "bdew.jeibees.mutation." + root.getUID)
        JEIBees.logInfo("Added %d mutation recipes for %s", recipes.length, root.getUID.replace("root", ""))
      } else {
        JEIBees.logInfo("Not adding mutation recipes for %s - disabled", root.getUID.replace("root", ""))
      }

      if (cfg.showProduce) {
        val species = root.getIndividualTemplates.map(_.getGenome.getPrimary)
        val recipes = species.map(new ProduceRecipe(_)).filter(_.hasProducts)
        registry.addRecipes(recipes, "bdew.jeibees.produce." + root.getUID)
        JEIBees.logInfo("Added %d produce recipes for %s", recipes.length, root.getUID.replace("root", ""))
      } else {
        JEIBees.logInfo("Not adding produce recipes for %s - disabled", root.getUID.replace("root", ""))
      }
    }
  }
}
