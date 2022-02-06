package net.bdew.jeibees.misc

import mezz.jei.api.registration.IRecipeRegistration

trait RecipesProvider {
  def registerRecipes(reg: IRecipeRegistration): Unit
}
