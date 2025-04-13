package com.example.drivenextmobile.app.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object Supabase {
    val client = createSupabaseClient(
        supabaseUrl = "https://oshagjvedurgivloywgj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9zaGFnanZlZHVyZ2l2bG95d2dqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE1MzQ3MTUsImV4cCI6MjA1NzExMDcxNX0.TS1rz0iLbALUOHEG09TxcUX4YTzfps3FhlZ2R_ksEN0"
    ) {
        install(Postgrest)
    }
}