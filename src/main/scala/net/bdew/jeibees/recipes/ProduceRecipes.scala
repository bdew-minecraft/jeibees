package net.bdew.jeibees.recipes

import com.mojang.blaze3d.matrix.MatrixStack
import genetics.api.alleles.IAlleleSpecies
import genetics.api.individual.IIndividual
import genetics.api.root.IIndividualRoot
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.category.IRecipeCategory
import mezz.jei.api.registration.IRecipeRegistration
import net.bdew.jeibees.gui.{StackWithChanceRenderer, TextRender}
import net.bdew.jeibees.misc.{GeneticsHelper, ItemHelper, RecipesProvider, Slot}
import net.bdew.jeibees.{BeesJEIPlugin, JEIBees}
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.{ITextComponent, TranslationTextComponent}

import java.util
import java.util.Locale
import scala.jdk.CollectionConverters._

case class ProduceRecipe(species: IAlleleSpecies) {
  val inputs: util.List[util.List[ItemStack]] = List(GeneticsHelper.getAllItemsFromSpecies(species).asJava).asJava
  val producer = GeneticsHelper.getProducerStack(species)
  val (produce, specialty) = GeneticsHelper.getProduceAndSpecialty(species)
  val produceSanitized: List[(ItemStack, Float)] = ItemHelper.mergeStacks(ItemHelper.sanitizeDrops(produce, species.getRegistryName.toString + " drops")).toList.sortBy(-_._2)
  val specialtySanitized: List[(ItemStack, Float)] = ItemHelper.mergeStacks(ItemHelper.sanitizeDrops(specialty, species.getRegistryName.toString + " specialty")).toList.sortBy(-_._2)
  val outputs: util.List[ItemStack] = (produceSanitized.map(_._1) ++ specialtySanitized.map(_._1)).asJava
  val hasProduce: Boolean = !outputs.isEmpty
}

class ProduceRecipes[I <: IIndividual](root: IIndividualRoot[I], iconItem: ItemStack) extends RecipesProvider with IRecipeCategory[ProduceRecipe] {
  val rootUid: String = root.getUID.replace("root", "").toLowerCase(Locale.US)

  override val getUid: ResourceLocation = new ResourceLocation(JEIBees.ModId, s"produce.${rootUid}")
  override val getRecipeClass: Class[_ <: ProduceRecipe] = classOf[ProduceRecipe]

  override val getTitle: String = null
  override val getTitleAsTextComponent: ITextComponent = new TranslationTextComponent(s"bdew.jeibees.produce.${rootUid}")

  override def getBackground: IDrawable =
    BeesJEIPlugin.guiHelper.drawableBuilder(
      new ResourceLocation(JEIBees.ModId, "textures/recipes.png"),
      0, 61, 162, 61
    ).build()

  override def getIcon: IDrawable =
    BeesJEIPlugin.guiHelper.createDrawableIngredient(iconItem)

  override def setIngredients(recipe: ProduceRecipe, ingredients: IIngredients): Unit = {
    ingredients.setInputLists(VanillaTypes.ITEM, recipe.inputs)
    ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
  }

  override def setRecipe(recipeLayout: IRecipeLayout, recipe: ProduceRecipe, ingredients: IIngredients): Unit = {
    val itemStacks = recipeLayout.getItemStacks

    itemStacks.init(0, ProduceRecipes.inputSlot.isInput, ProduceRecipes.inputSlot.x, ProduceRecipes.inputSlot.y)
    itemStacks.set(0, recipe.producer)

    val rendererSpec = new StackWithChanceRenderer(recipe.specialtySanitized.toMap)
    val rendererProd = new StackWithChanceRenderer(recipe.produceSanitized.toMap)

    for (((stacks, Slot(x, y, isInput)), slot) <- ItemHelper.outputsList(recipe.produceSanitized, ProduceRecipes.produceSlots.length).zip(ProduceRecipes.produceSlots).zipWithIndex) {
      itemStacks.init(slot + 1, isInput, rendererProd, x, y, 18, 18, 1, 1)
      itemStacks.set(slot + 1, stacks)
    }

    for (((stacks, Slot(x, y, isInput)), slot) <- ItemHelper.outputsList(recipe.specialtySanitized, ProduceRecipes.specialtySlots.length).zip(ProduceRecipes.specialtySlots).zipWithIndex) {
      itemStacks.init(slot + 4, isInput, rendererSpec, x, y, 18, 18, 1, 1)
      itemStacks.set(slot + 4, stacks)
    }
  }

  override def draw(recipe: ProduceRecipe, ps: MatrixStack, mouseX: Double, mouseY: Double): Unit = {
    TextRender.drawCenteredMulti(ps, recipe.species.getDisplayName,
      ProduceRecipes.inputSlot.x + 9, ProduceRecipes.inputSlot.y + 22, 0xFFFFFFFF)
  }

  def registerRecipes(reg: IRecipeRegistration): Unit = {
    val allRecipes = root.getIndividualTemplates.asScala
      .map(template => ProduceRecipe(template.getGenome.getPrimary))
      .filter(_.hasProduce).asJava
    JEIBees.logInfo("Adding %d produce recipes for %s", allRecipes.size, root.getUID)
    reg.addRecipes(allRecipes, getUid)
  }
}

object ProduceRecipes {
  val inputSlot: Slot = Slot(18, 15, true)
  val produceSlots: List[Slot] = List(
    Slot(92, 4, false),
    Slot(114, 4, false),
    Slot(136, 4, false),
  )
  val specialtySlots: List[Slot] = List(
    Slot(92, 32, false),
    Slot(114, 32, false),
    Slot(136, 32, false),
  )
}