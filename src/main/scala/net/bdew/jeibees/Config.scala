package net.bdew.jeibees

import genetics.api.root.IIndividualRoot
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

import java.util.Locale
import scala.jdk.CollectionConverters._

object Config {
  private val builder = new ForgeConfigSpec.Builder

  val showRequirements: ForgeConfigSpec.BooleanValue =
    builder.comment("Set to false to disable display of mutation requirements")
      .define("showRequirements", true)

  val showSecretMutations: ForgeConfigSpec.BooleanValue =
    builder.comment("Set to false to disable display of secret mutations")
      .define("showSecretMutations", true)

  val identifyGenome: ForgeConfigSpec.BooleanValue =
    builder.comment("Set to false to disable showing identified genome in recipes")
      .define("identifyGenome", true)

  private val skipMutationsFor =
    builder.comment("List of classes of species to ignore in mutation recipes (e.g. bees, trees, butterflies)")
      .defineList("skipMutationsFor", List.empty[String].asJava, _ => true)

  private val skipProduceFor =
    builder.comment("List of classes of species to ignore in produce recipes (e.g. bees, trees, butterflies)")
      .defineList("skipProduceFor", List("butterflies").asJava, _ => true)

  def shouldShowMutations(root: IIndividualRoot[_]): Boolean =
    !skipMutationsFor.get().contains(root.getUID.replace("root", "").toLowerCase(Locale.US))

  def shouldShowProduce(root: IIndividualRoot[_]): Boolean =
    !skipProduceFor.get().contains(root.getUID.replace("root", "").toLowerCase(Locale.US))

  val CLIENT: ForgeConfigSpec = builder.build()

  def init(): Unit = {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT)
  }
}
