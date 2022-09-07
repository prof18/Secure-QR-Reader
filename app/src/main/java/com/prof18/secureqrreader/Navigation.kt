/*
 * Copyright 2022 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prof18.secureqrreader

internal sealed class Screen(val name: String) {
    object Splash : Screen("splash_screen")
    object WelcomeScreen : Screen("welcome_screen")
    object ScanScreen : Screen("scan_screen")
    object ResultScreen : Screen("result_screen")
    object AboutScreen : Screen("about_screen")
    object LibrariesScreen: Screen("libraries_screen")
}
