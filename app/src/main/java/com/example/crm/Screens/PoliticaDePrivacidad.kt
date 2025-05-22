package com.example.crm.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PoliticaDePrivacidad(navHostController: NavHostController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                modifier = Modifier
                    .clickable { navHostController.popBackStack() }
                    .padding(end = 12.dp)
            )
            Text(
                text = "Política de Privacidad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fecha de actualización
        Text(
            text = "Última actualización: 14/08/2024",
            fontSize = 12.sp,
            color = Color(0xFF73A1F7)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Introducción
        Text(
            text = "Esta Política de Privacidad describe cómo recopilamos, usamos y protegemos su información personal cuando utiliza nuestra aplicación. Al utilizar este servicio, usted acepta las prácticas descritas a continuación.",
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nos comprometemos a garantizar que su privacidad esté protegida. En caso de que le pidamos proporcionar cierta información mediante la cual pueda ser identificado, puede estar seguro de que solo se usará de acuerdo con esta declaración de privacidad.",
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Términos y condiciones
        Text(
            text = "Términos y Condiciones",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004AAD)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val condiciones = listOf(
            "Información que recopilamos: Podemos recopilar datos personales como nombre, dirección de correo electrónico, número de teléfono y otra información relevante para mejorar su experiencia en la aplicación.",
            "Uso de la información: Utilizamos la información para proporcionarle un mejor servicio, personalizar el contenido, mejorar nuestros productos y, en algunos casos, enviar comunicaciones promocionales.",
            "Seguridad: Nos comprometemos a garantizar que su información esté segura. Implementamos medidas físicas, electrónicas y administrativas adecuadas para evitar el acceso no autorizado o divulgación.",
            "Control sobre su información: Puede optar por restringir la recopilación o el uso de su información personal en cualquier momento. Puede solicitar acceso, rectificación o eliminación de sus datos contactando con nosotros.",
            "Cookies: Nuestra aplicación no utiliza cookies, pero ciertos servicios de terceros integrados podrían hacerlo. Consulte sus respectivas políticas de privacidad para más información.",
            "Modificaciones: Nos reservamos el derecho de modificar esta política en cualquier momento. Se recomienda revisar esta página periódicamente para asegurarse de estar de acuerdo con cualquier cambio."
        )

        condiciones.forEachIndexed { index, texto ->
            Text(
                text = "${index + 1}. $texto",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
