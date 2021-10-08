[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.warting.permissionsui/permissionsui/badge.png)](https://maven-badges.herokuapp.com/maven-central/se.warting.permissionsui/permissionsui)

# Permission UI

A set of Android-UI components to make it easier to request permission in a user friendly way.

## Access background location 
A jetpack compose module to easy request background location permission in a intuitive way.

![happy case gif](screenshot/sdk12_happy_case.gif)

## How to include in your project
The library is available via MavenCentral:
```
allprojects {
    repositories {
        // ...
        mavenCentral()
    }
}
```

Add it to your module dependencies:
```
dependencies {
    implementation("se.warting.permissionsui:permissionsui:<latest_version>")
}
```

## How to use
All you need to do is to call `LocationInBackgroundTutorialView`:
```
LocationInBackgroundTutorialView() {
    // Permissions granted 
}
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
                    LocationInBackgroundTutorialView() {
                        // Permissions granted 
                    }
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
