package com.gksenon.sleepdiary.domain

import java.util.Date
import java.util.UUID

data class Sleep(val id: UUID, val start: Date, val end: Date)