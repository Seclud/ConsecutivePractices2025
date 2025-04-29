package com.example.consecutivepractice

import androidx.compose.runtime.mutableStateOf

object GamesData {
    val games = mutableStateOf<List<Game>>(generateMockGames())

    private fun generateMockGames(): List<Game> {
        return listOf(
            Game(
                id = "1",
                title = "The Witcher 3: Wild Hunt",
                description = "Ролевая игра с открытым миром в фэнтезийной вселенной, полной осмысленного выбора и серьезных последствий.",
                releaseDate = "2015",
                rating = 9.3,
                genres = listOf("RPG", "Экшен", "Открытый мир"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202212/0814/9uU0gBq02jmXHtDsm82AV722.jpg",
                developer = "CD Projekt Red",
                platform = listOf("PC", "PlayStation", "Xbox", "Switch"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            ),
            Game(
                id = "2",
                title = "Red Dead Redemption 2",
                description = "Эпическая история о жизни в неумолимом американском сердце. Огромный и атмосферный мир игры служит основой для совершенно нового многопользовательского онлайнового опыта.Ролевая игра с открытым миром в фэнтезийной вселенной, полной осмысленного выбора и серьезных последствий.",
                releaseDate = "2018",
                rating = 9.2,
                genres = listOf("Экшен Адвенчура", "Открытый мир", "Вестерн"),
                coverImage = "https://image.api.playstation.com/cdn/UP1004/CUSA03041_00/Hpl5MtwQgOVF9vJqlfui6SDB5Jl4oBSq.png",
                developer = "Rockstar Games",
                platform = listOf("PC", "PlayStation", "Xbox"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            ),
            Game(
                id = "3",
                title = "God of War",
                description = "Месть богам Олимпа осталась позади, и теперь Кратос живет как человек в царстве норвежских богов и чудовищ.",
                releaseDate = "2018",
                rating = 9.0,
                genres = listOf("Экшен", "Адвенчура", "RPG"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202207/1210/4xJ8XB3bi888QTLZYdl7Oi0s.png",
                developer = "Santa Monica Studio",
                platform = listOf("PlayStation", "PC"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            ),
            Game(
                id = "4",
                title = "The Legend of Zelda: Breath of the Wild",
                description = "Отправляйтесь в мир открытий, исследований и приключений в этом приключении под открытым небом по огромному миру.",
                releaseDate = "2017",
                rating = 9.5,
                genres = listOf("Экшен Адвенчура", "Открытый мир"),
                coverImage = "https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000000025/7137262b5a64d921e193653f8aa0b722925abc5680380ca0e18a5cfd91697f58",
                developer = "Nintendo",
                platform = listOf("Nintendo Switch")
            ),
            Game(
                id = "5",
                title = "Elden Ring",
                description = "Экшен-РПГ и фэнтезийный мир Джорджа Р. Р. Мартина создают новую темную фэнтезийную вселенную.",
                releaseDate = "2022",
                rating = 9.3,
                genres = listOf("Экшен RPG", "Открытый мир"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202110/2000/phvVT0qZfcRms5qDAk0SI3CM.png",
                developer = "FromSoftware",
                platform = listOf("PC", "PlayStation", "Xbox"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            ),
            Game(
                id = "6",
                title = "Cyberpunk 2077",
                description = "Действие приключенческого экшена с открытым миром разворачивается в Ночном городе - мегаполисе, одержимом властью, гламуром и модификацией тела.",
                releaseDate = "2020",
                rating = 7.1,
                genres = listOf("RPG", "Открытый мир", "Экшен"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202111/3013/bxSj4jO0KBqUgAbH3zuNjCje.jpg",
                developer = "CD Projekt Red",
                platform = listOf("PC", "PlayStation", "Xbox"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            ),
            Game(
                id = "7",
                title = "Horizon Forbidden West",
                description = "Присоединяйтесь к Алой, когда она отправится на Запретный Запад - величественную, но опасную границу, таящую в себе новые загадочные угрозы..",
                releaseDate = "2022",
                rating = 8.8,
                genres = listOf("Экшен RPG", "Открытый мир"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202107/3100/HO8vkO9pfXhwbHi5WHECQJdN.png",
                developer = "Guerrilla Games",
                platform = listOf("PlayStation")
            ),
            Game(
                id = "8",
                title = "Halo Infinite",
                description = "The Master Chief returns in the next chapter of the legendary franchise with the most expansive Master Chief campaign yet.",
                releaseDate = "2021",
                rating = 8.7,
                genres = listOf("Шутер от первого лица", "Экшен"),
                coverImage = "https://store-images.s-microsoft.com/image/apps.21536.13727851868390641.c9cc5f66-aff8-406c-af6b-440838730be0.68796bde-cbf5-4eaa-a299-011417041da6?q=90&w=177&h=265",
                developer = "343 Industries",
                platform = listOf("Xbox", "PC"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            ),
            Game(
                id = "9",
                title = "Ghost of Tsushima",
                description = "В конце 13 века Монгольская империя опустошила целые государства. Остров Цусима - это все, что стоит между материковой частью Японии и массовым монгольским нашествием.",
                releaseDate = "2020",
                rating = 9.0,
                genres = listOf("Экшен", "Адвенчура", "Открытый мир"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202010/0222/niMUubpU9y1PxNvYmDfb8QFD.png",
                developer = "Sucker Punch Productions",
                platform = listOf("PlayStation")
            ),
            Game(
                id = "10",
                title = "Marvel's Spider-Man",
                description = "Это не тот Человек-паук, которого вы встречали или видели раньше. Это опытный Питер Паркер, который мастерски борется с преступлениями в марвеловском Нью-Йорке.",
                releaseDate = "2018",
                rating = 8.8,
                genres = listOf("Экшен Адвенчура", "Открытый мир", "Супергероика"),
                coverImage = "https://image.api.playstation.com/vulcan/ap/rnd/202009/3021/QeJWAaLcYNOpCv7yCVZZEOY5.jpg",
                developer = "Insomniac Games",
                platform = listOf("PlayStation", "PC"),
                systemRequirements = SystemRequirements(
                    minimum = mapOf(
                        "OS" to "Windows 10 (64-bit)",
                        "CPU" to "Intel Core i5-8400 or AMD Ryzen 3 3300X",
                        "RAM" to "12 GB",
                        "GPU" to "Nvidia GeForce GTX 1060 3GB or AMD Radeon RX 580 4GB",
                        "Storage" to "60 GB"
                    ),
                    recommended = mapOf(
                        "OS" to "Windows 10/11 (64-bit)",
                        "CPU" to "Intel Core i7-8700K or AMD Ryzen 5 3600X",
                        "RAM" to "16 GB",
                        "GPU" to "Nvidia GeForce GTX 1070 8GB or AMD Radeon RX Vega 56 8GB",
                        "Storage" to "60 GB SSD"
                    )
                )
            )
        )
    }
}
