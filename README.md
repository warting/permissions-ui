# Background Location Permission Rationale (Blpr)

Jetpack Compose module to easy request background location permission in a intuitive way.

Until this package is deployed to maven central this can be accessed from our private maven repository 


![Screenshots](screenshot/screens.png)


## Add Maven repository
```
repositories {
    maven(url = "https://premex.jfrog.io/artifactory/premex/")
}
```

## Download
```
dependencies {
    implementation("com.github.warting:blpr:<latest_version>")
}
```

## How to use
All you need to do is to call `LocationInBackgroundTutorialView`:
```
LocationInBackgroundTutorialView()
```


Complete example:
```
import se.warting.blpr.geo.LocationInBackgroundTutorialView

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

## Notes
This Library adds the following permissions to your app: 
- android.permission.ACCESS_COARSE_LOCATION
- android.permission.ACCESS_FINE_LOCATION
- android.permission.ACCESS_BACKGROUND_LOCATION


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