# hyundainuevo

Launcher Android para televisores Hyundai con Android 7.1.1 (API 25).

## Contenido
- Proyecto Android Java + Gradle.
- Recursos extraídos de la imagen de referencia en `app/src/main/res/drawable-nodpi/`.
- Pantalla de referencia a pantalla completa para conservar la composición visual.
- Zonas interactivas para mando: TV, HDMI, USB, Aplicaciones, ajustes, caché, gestión de apps, Wi-Fi y favoritos.
- GitHub Actions genera el artefacto APK llamado `hyundainuevo`.

## Compilar
Sube el contenido completo de este ZIP a la raíz de un repositorio GitHub. Entra a Actions y ejecuta `Build hyundainuevo APK`. Al terminar, descarga el artefacto `hyundainuevo`.

## Notas de funcionamiento
- El diseño de referencia se muestra como imagen y las zonas invisibles superpuestas responden al mando/OK.
- Los iconos favoritos intentan abrir los paquetes correspondientes; la disponibilidad depende de las apps instaladas.
- El acceso a HDMI/TV depende de las APIs y aplicaciones que exponga el firmware Realtek.
- Android 7.1.1 no permite limpiar automáticamente la caché privada de todas las apps desde una aplicación normal; el botón abre información y ajustes.
- No se ha ejecutado una compilación real en este entorno; valida GitHub Actions antes de instalar.
