package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.xstore2.XtbServiceAsync
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val login = "12263751"
        val password = "xoh26561"

        val x: XtbServiceAsync =
            XtbServiceAsync(login, password);


        // START
        println("<---------------------XtbAsyncService--------------------->");
        println("<-----------------Request->Response Methods--------------------->");
        x.connectAsync();

        val allSymbolsAsync_result = x.allSymbolsAsync
        val pingAsync_result = x.pingAsync
        val getProfitCalculationAsync_result =
            x.getProfitCalculationAsync(1.1.toFloat(), 1, 1.1.toFloat(), "USDPLN", 1.1.toFloat())
        val getSymbolAyns_result = x.getSymbolAsync("USDPLN")

        println(getSymbolAyns_result.get())
        println(getProfitCalculationAsync_result.get())
        println(pingAsync_result.get())
        println(allSymbolsAsync_result.get())

        println("<-------------END Request->Response Methods--------------------->");
        println("<-------------Streaming Methods--------------------->");
        x.subscribeGetKeepAlive()
        x.subscribeGetTicketPrice("USDPLN")
        x.subscribeGetTicketPrice("EURUSD")
        x.subscribeGetTicketPrice("EURPLN")
        val subscriptionResponsesQueue = x.subscriptionResponsesQueue

        //x.disconnectAsync();
        val runnable = Runnable {
            while(x.isConnected.get()){
                val response = subscriptionResponsesQueue.take().toString()
                println(response)
            }
        }
        Thread(runnable).start()
        // END


        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}