package net.bdew.jeibees

import forestry.api.apiculture.genetics.IBeeRoot
import forestry.api.arboriculture.genetics.ITreeRoot
import mezz.jei.api._
import mezz.jei.api.helpers.{IGuiHelper, IJeiHelpers}
import mezz.jei.api.registration.{IRecipeCategoryRegistration, IRecipeRegistration}
import mezz.jei.api.runtime.IIngredientManager
import net.bdew.jeibees.misc.{GeneticsHelper, RecipesProvider}
import net.bdew.jeibees.recipes.{MutationRecipes, ProduceRecipes}
import net.minecraft.item.{ItemStack, Items}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

@JeiPlugin
class BeesJEIPlugin extends IModPlugin {
  override def getPluginUid: ResourceLocation = new ResourceLocation(JEIBees.ModId, "jei")

  var categories: List[RecipesProvider] = List.empty

  override def registerCategories(registry: IRecipeCategoryRegistration): Unit = {
    BeesJEIPlugin.helpers = registry.getJeiHelpers

    val catsBuilder = List.newBuilder[RecipesProvider]

    for (root <- GeneticsHelper.activeRoots) {

      if (Config.shouldShowMutations(root)) {
        val iconItem = GeneticsHelper.getDefaultMemberStack(root)
        val cat = new MutationRecipes(root, iconItem)
        registry.addRecipeCategories(cat)
        catsBuilder += cat
      } else {
        JEIBees.logInfo(s"Skipping mutations for ${root.getUID} due to config")
      }


      if (Config.shouldShowProduce(root)) {
        val iconItem = root match {
          case _: IBeeRoot => new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "bee_comb_honey")))
          case _: ITreeRoot => new ItemStack(Items.APPLE)
          case _ => new ItemStack(Items.BUCKET) // Fallback for unknown types
        }
        val cat = new ProduceRecipes(root, iconItem)
        registry.addRecipeCategories(cat)
        catsBuilder += cat
      } else {
        JEIBees.logInfo(s"Skipping produce for ${root.getUID} due to config")
      }

    }

    categories = catsBuilder.result()
  }

  override def registerRecipes(registration: IRecipeRegistration): Unit = {
    categories.foreach(_.registerRecipes(registration))
    BeesJEIPlugin.ingredientHelper = registration.getIngredientManager
  }
}

object BeesJEIPlugin {
  var helpers: IJeiHelpers = _
  var ingredientHelper: IIngredientManager = _
  def guiHelper: IGuiHelper = helpers.getGuiHelper
}