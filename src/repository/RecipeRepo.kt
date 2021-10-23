package ru.skovoroda.repository

import org.jetbrains.exposed.sql.*
import ru.skovoroda.data.model.Recipe
import ru.skovoroda.data.table.RecipeTable
import javax.xml.crypto.Data

class RecipeRepo {

    suspend fun addRecipe(recipe: Recipe, email:String){
        DatabaseFactory.dbQuery {
            RecipeTable.insert { table->
                table[RecipeTable.id] = recipe.id
                table[RecipeTable.title] = recipe.title
                table[RecipeTable.content] = recipe.content
                table[RecipeTable.timeStamp] = recipe.timeStamp
                table[RecipeTable.userEmail] = email
            }
        }
    }

    suspend fun getAllRecipes(email: String):List<Recipe>  =
        DatabaseFactory.dbQuery {
            RecipeTable.select{
                RecipeTable.userEmail.eq(email)
            }.mapNotNull {
                rowToRecipe(it)
            }
        }

    suspend fun udpateRecipe(recipe: Recipe, email: String){
        DatabaseFactory.dbQuery {
            RecipeTable.update(
                where = {
                    RecipeTable.userEmail.eq(email) and RecipeTable.id.eq(recipe.id)
                },
                body = { table ->
                    table[RecipeTable.title] = recipe.title
                    table[RecipeTable.content] = recipe.content
                    table[RecipeTable.timeStamp] = recipe.timeStamp
                }
            )
        }
    }

    suspend fun deleteRecipe(recipeId:String){
        DatabaseFactory.dbQuery {
            RecipeTable.deleteWhere {
                RecipeTable.id.eq(recipeId)
            }
        }
    }


    private fun rowToRecipe(resultRow: ResultRow?):Recipe? {
        if(resultRow == null){
            return null
        }
        else{
            return Recipe(
                id = resultRow[RecipeTable.id],
                timeStamp = resultRow[RecipeTable.timeStamp],
                title = resultRow[RecipeTable.title],
                content = resultRow[RecipeTable.content],

            )
        }
    }

}