package ru.skovoroda.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.skovoroda.data.model.User
import ru.skovoroda.data.table.UserTable

class UserRepo {


    suspend fun addUser(user: User) =
        DatabaseFactory.dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.name] = user.name
                ut[UserTable.email] = user.email
                ut[UserTable.password] = user.password
            }
        }


    suspend fun findUserByEmail(email: String) = DatabaseFactory.dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }

        return User(
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password]
        )
    }


}
