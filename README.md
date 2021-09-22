# Permission UI

A set of Android-UI components to make it easier to request permission in a user friendly way.

## Access background location 
A jetpack compose module to easy request background location permission in a intuitive way.

![happy case gif](screenshot/sdk12_happy_case.gif)

## Add Maven repository
Until this package is deployed to maven central this can be accessed from our private maven repository
```
repositories {
    maven(url = "https://premex.jfrog.io/artifactory/premex/")
}
```

## Download
```
dependencies {
    implementation("com.github.warting:permissionsui:<latest_version>")
}
```

## How to use
All you need to do is to call `LocationInBackgroundTutorialView`:
```
LocationInBackgroundTutorialView()
```


Example:
```
import se.warting.permissionsui.backgroundlocation.LocationInBackgroundTutorialView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackgroundLocationPermissionRationaleTheme {
                Surface(color = MaterialTheme.colors.background) {
                    LocationInBackgroundTutorialView()
                }
            }
        }
    }
}
```
For a full implementation see: [Full sample](app/src/main/java/se/warting/backgroundlocationpermissionrationale/MainActivity.kt)

## Notes
This Library adds the following permissions to your app: 
- android.permission.ACCESS_COARSE_LOCATION
- android.permission.ACCESS_FINE_LOCATION
- android.permission.ACCESS_BACKGROUND_LOCATION

## Samples
| SDK | Happy case | Rationale case | Worst case |
| --- | --- | --- | --- |
| 10 | ![happy case gif](screenshot/sdk10_happy_case.gif) | ![rationale case gif](screenshot/sdk10_rationale_case.gif) | ![worst case gif](screenshot/sdk10_worst_case.gif) |
| 12 | ![happy case gif](screenshot/sdk12_happy_case.gif) | ![rationale case gif](screenshot/sdk12_rationale_case.gif) | ![worst case gif](screenshot/sdk12_worst_case.gif) |

## License
```
MIT License

Copyright (c) 2021 Stefan Wärting

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```