

package se.warting.permissionsui.backgroundlocation

internal sealed class ViewState<T> {
    class Loading<T> : ViewState<T>()
    data class Success<T>(val data: T) : ViewState<T>()
}
