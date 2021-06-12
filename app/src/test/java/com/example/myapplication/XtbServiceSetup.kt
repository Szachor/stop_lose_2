import com.example.myapplication.xstore2.XtbServiceAsync
import junit.framework.Assert.assertEquals
import org.junit.Test

class XtbServiceSetupTest(
    private val login: String = "12263751",
    private val password: String = "xoh26561"
) {

    @Test
    fun connection_test() {
        val xtbService = XtbServiceAsync(login, password)
        xtbService.connectAsync()
        val isConnected = xtbService.isConnected
        assertEquals(true, isConnected.get())
    }

    @Test
    fun disconnection_test() {
        val xtbService = XtbServiceAsync(login, password)
        xtbService.connectAsync()
        var isConnected = xtbService.isConnected
        assertEquals(true, isConnected.get())
        xtbService.disconnectAsync()
        isConnected = xtbService.isConnected
        assertEquals(false, isConnected.get())
    }
}