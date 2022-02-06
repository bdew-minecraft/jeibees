package net.bdew.jeibees.recipes

import com.mojang.blaze3d.matrix.MatrixStack
import genetics.api.individual.IIndividual
import genetics.api.mutation.{IMutation, IMutationContainer}
import genetics.api.root.IIndividualRoot
import genetics.api.root.components.ComponentKeys
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.category.IRecipeCategory
import mezz.jei.api.registration.IRecipeRegistration
import net.bdew.jeibees.gui.TextRender
import net.bdew.jeibees.misc.{GeneticsHelper, RecipesProvider, Slot}
import net.bdew.jeibees.{BeesJEIPlugin, Config, JEIBees}
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.{ITextComponent, TranslationTextComponent}

import java.util
import java.util.{Collections, Locale}
import scala.jdk.CollectionConverters._

case class MutationRecipe(val mutation: IMutation) {
  val p1stack: ItemStack = GeneticsHelper.getItemFromSpecies(mutation.getFirstParent, GeneticsHelper.Position.P1)
  val p2stack: ItemStack = GeneticsHelper.getItemFromSpecies(mutation.getSecondParent, GeneticsHelper.Position.P2)
  val resStack: ItemStack = GeneticsHelper.getItemFromTemplate(mutation.getRoot, mutation.getTemplate, GeneticsHelper.Position.RES)
  val allInputs: util.List[ItemStack] = (GeneticsHelper.getAllItemsFromSpecies(mutation.getFirstParent) ++ GeneticsHelper.getAllItemsFromSpecies(mutation.getSecondParent)).asJava
  val allOutputs: util.List[ItemStack] = GeneticsHelper.getAllItemsFromTemplate(mutation.getRoot, mutation.getTemplate).asJava
  val hasRequirements: Boolean = !mutation.getSpecialConditions.isEmpty && Config.showRequirements.get()
}

class MutationRecipes[I <: IIndividual](root: IIndividualRoot[I], iconItem: ItemStack) extends RecipesProvider with IRecipeCategory[MutationRecipe] {
  val rootUid: String = root.getUID.replace("root", "").toLowerCase(Locale.US)

  override val getUid: ResourceLocation = new ResourceLocation(JEIBees.ModId, s"mutation.${rootUid}")
  override val getRecipeClass: Class[_ <: MutationRecipe] = classOf[MutationRecipe]

  override val getTitle: String = null
  override val getTitleAsTextComponent: ITextComponent = new TranslationTextComponent(s"bdew.jeibees.mutation.${rootUid}")

  override def getBackground: IDrawable =
    BeesJEIPlugin.guiHelper.drawableBuilder(
      new ResourceLocation(JEIBees.ModId, "textures/recipes.png"),
      0, 0, 162, 61
    ).build()

  override def getIcon: IDrawable =
    BeesJEIPlugin.guiHelper.createDrawableIngredient(iconItem)

  override def setIngredients(recipe: MutationRecipe, ingredients: IIngredients): Unit = {
    ingredients.setInputs(VanillaTypes.ITEM, recipe.allInputs)
    ingredients.setOutputs(VanillaTypes.ITEM, recipe.allOutputs)
  }

  override def setRecipe(recipeLayout: IRecipeLayout, recipe: MutationRecipe, ingredients: IIngredients): Unit = {
    val itemStacks = recipeLayout.getItemStacks

    MutationRecipes.slots foreach { case (slot, Slot(x, y, isInput)) =>
      itemStacks.init(slot, isInput, x, y)
    }

    itemStacks.set(0, recipe.p1stack)
    itemStacks.set(1, recipe.p2stack)
    itemStacks.set(2, recipe.resStack)
  }

  override def draw(recipe: MutationRecipe, ps: MatrixStack, mouseX: Double, mouseY: Double): Unit = {

    TextRender.drawCenteredMulti(ps,
      recipe.mutation.getFirstParent.getDisplayName,
      MutationRecipes.slots(0).x + 9, MutationRecipes.slots(0).y + 22, 0xFFFFFFFF
    )

    TextRender.drawCenteredMulti(ps,
      recipe.mutation.getSecondParent.getDisplayName,
      MutationRecipes.slots(1).x + 9, MutationRecipes.slots(1).y + 22, 0xFFFFFFFF
    )

    TextRender.drawCenteredMulti(ps,
      recipe.mutation.getResultingSpecies.getDisplayName,
      MutationRecipes.slots(2).x + 9, MutationRecipes.slots(2).y + 22, 0xFFFFFFFF
    )

    if (recipe.hasRequirements) {
      TextRender.drawCentered(ps, "[%.0f%%]".format(recipe.mutation.getBaseChance), 105, 12, 0xFFFFFF)
    } else {
      TextRender.drawCentered(ps, "%.0f%%".format(recipe.mutation.getBaseChance), 105, 12, 0xFFFFFF)
    }
  }

  override def getTooltipStrings(recipe: MutationRecipe, mouseX: Double, mouseY: Double): util.List[ITextComponent] = {
    if (recipe.hasRequirements && mouseX >= 90 && mouseX <= 120 && mouseY >= 11 && mouseY <= 19)
      new util.ArrayList[ITextComponent](recipe.mutation.getSpecialConditions)
    else
      Collections.emptyList
  }

  def registerRecipes(reg: IRecipeRegistration): Unit = {
    val mutations: IMutationContainer[I, _ <: IMutation] = root.getComponent(ComponentKeys.MUTATIONS)
    var mutationList = mutations.getMutations(false).asScala
    if (!Config.showSecretMutations.get()) mutationList = mutationList.filter(x => !x.isSecret)
    val allRecipes = mutationList.map(mutation => MutationRecipe(mutation)).asJava
    JEIBees.logInfo("Adding %d mutation recipes for %s", allRecipes.size, root.getUID)
    reg.addRecipes(allRecipes, getUid)
  }
}

object MutationRecipes {
  val slots: Map[Int, Slot] = Map(
    0 -> Slot(18, 15, true),
    1 -> Slot(71, 15, true),
    2 -> Slot(125, 15, false)
  )
}