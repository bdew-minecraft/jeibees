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

package net.bdew.jeibees

import forestry.api.genetics.AlleleManager
import mezz.jei.api.{BlankModPlugin, IModRegistry, ISubtypeRegistry, JEIPlugin}
import net.bdew.jeibees.misc.GeneticSubtypeInterpreter
import net.bdew.jeibees.recipes.mutation.{MutationRecipe, MutationRecipeCategory, MutationRecipeHandler}
import net.bdew.jeibees.recipes.produce.{ProduceRecipe, ProduceRecipeCategory, ProduceRecipeHandler}
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

import scala.collection.JavaConversions._

@JEIPlugin
class BeesJEIPlugin extends BlankModPlugin {
  val geneticsItems = List(
    //    new ResourceLocation("forestry", "beeDroneGE"),
    //    new ResourceLocation("forestry", "beePrincessGE"),
    //    new ResourceLocation("forestry", "beeQueenGE"),
    new ResourceLocation("forestry", "beeLarvaeGE"),
    new ResourceLocation("forestry", "sapling"),
    new ResourceLocation("forestry", "pollenFertile"),
    new ResourceLocation("forestry", "butterflyGE"),
    new ResourceLocation("forestry", "caterpillarGE"),
    new ResourceLocation("forestry", "cocoonGE"),
    new ResourceLocation("forestry", "serumGE")
  )

  override def registerItemSubtypes(subtypeRegistry: ISubtypeRegistry): Unit = {
    for (id <- geneticsItems; item <- Option(Item.REGISTRY.getObject(id))) {
      subtypeRegistry.registerNbtInterpreter(item, GeneticSubtypeInterpreter)
      JEIBees.logInfo("Added subtype interpreter for: %s (%s)", id, item.getUnlocalizedName)
    }
  }

  override def register(registry: IModRegistry): Unit = {
    JEIBees.logInfo("JEI Plugin initializing")

    val guiHelper = registry.getJeiHelpers.getGuiHelper

    registry.addRecipeHandlers(MutationRecipeHandler)
    registry.addRecipeHandlers(ProduceRecipeHandler)

    for ((uid, root) <- AlleleManager.alleleRegistry.getSpeciesRoot) {
      val cfg = Config.rootConfigs.getOrElse(root, Config.disabledConfig)

      if (cfg.showMutations) {
        val category = new MutationRecipeCategory(root, guiHelper)
        var mutations = root.getMutations(false)
        if (!Config.showSecret)
          mutations = mutations.filterNot(_.isSecret)
        val recipes = mutations.map(new MutationRecipe(_, category)).toList
        registry.addRecipeCategories(category)
        registry.addRecipes(recipes)
        JEIBees.logInfo("Added %d mutation recipes for %s", recipes.length, root.getUID.replace("root", ""))
      } else {
        JEIBees.logInfo("Not adding mutation recipes for %s - disabled", root.getUID.replace("root", ""))
      }

      if (cfg.showProduce) {
        val category = new ProduceRecipeCategory(root, guiHelper)
        val species = root.getIndividualTemplates.map(_.getGenome.getPrimary)
        val recipes = species.map(new ProduceRecipe(_, category)).filter(_.hasProducts)
        registry.addRecipeCategories(category)
        registry.addRecipes(recipes)
        JEIBees.logInfo("Added %d produce recipes for %s", recipes.length, root.getUID.replace("root", ""))
      } else {
        JEIBees.logInfo("Not adding produce recipes for %s - disabled", root.getUID.replace("root", ""))
      }
    }
  }
}
