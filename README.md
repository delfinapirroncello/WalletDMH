# Digital Money House - Proyecto Wallet DH

## Descripción del Proyecto

Digital Money House es una billetera virtual que permite a los usuarios registrar cuentas, realizar transferencias, consultar actividades y gestionar tarjetas de crédito/débito. Este proyecto tiene como objetivo proporcionar un sistema seguro, escalable y funcional que permita a los usuarios gestionar su dinero a través de una plataforma digital.

## Objetivos del Proyecto

- **Registrar y autenticar usuarios**: Permitir a los usuarios crear cuentas, iniciar sesión y gestionar sus sesiones.
- **Transferencias de dinero**: Permitir a los usuarios transferir dinero entre cuentas o a través de tarjetas de débito/crédito.
- **Consulta de actividades y movimientos**: Los usuarios pueden consultar su historial de transacciones y actividades dentro de la billetera.
- **Gestión de tarjetas**: Los usuarios pueden asociar y gestionar tarjetas de crédito y débito con su cuenta.
- **Pruebas automatizadas**: Uso de RestAssured para realizar pruebas automatizadas de la API.

## Planificación y Actividades

### Sprint I
1. **Inicio, registro y acceso**:
   - API funcional de registro de usuario con asignación automática de CVU y alias.
   - API de login de usuarios con token.
   - API de logout para invalidar el token del usuario.

2. **Testing y calidad**:
   - Realización de pruebas exploratorias.
   - Plan de pruebas para el sprint, incluyendo la clasificación de casos de prueba.
   - Mantenimiento de casos de prueba y ejecución de suites.

3. **Infraestructura**:
   - Diseño de la infraestructura local con Git y Docker.
   - Implementación de microservicios.

### Sprint II
1. **Dashboard y perfil**:
   - APIs para mostrar saldo disponible y últimos movimientos de la cuenta.
   - APIs para consultar y actualizar los datos del perfil del usuario y la cuenta.

2. **Registro de tarjetas**:
   - APIs para gestionar tarjetas de crédito y débito, incluyendo la posibilidad de asociarlas a la cuenta del usuario.

3. **Testing y calidad**:
   - Actualización y mantenimiento de casos de prueba.
   - Automatización de pruebas utilizando RestAssured.

### Sprint III
1. **Actividad y transacciones**:
   - API para consultar todas las actividades realizadas por el usuario.
   - API para obtener detalles de una actividad específica.

2. **Ingreso de dinero**:
   - API para ingresar dinero en la cuenta desde tarjetas de crédito o débito.

3. **Testing y calidad**:
   - Realización de pruebas exploratorias sobre nuevas funcionalidades.
   - Actualización de las pruebas automatizadas con nuevos casos.

### Sprint IV
1. **Transferencias de dinero**:
   - API para realizar transferencias de dinero entre cuentas o a través de tarjetas.

2. **Testing y calidad**:
   - Ejecución de pruebas manuales y mantenimiento de casos de prueba.
   - Validación de la API utilizando Postman.

### Pruebas con Capturas

Las pruebas realizadas, junto con sus respectivas capturas, están documentadas en el siguiente enlace:

[Pruebas con Capturas](https://docs.google.com/document/d/1DEFwv0I8rDg1TcFkkJNs3Y9jTdYhXxenc7jmdHcPmYA/edit?usp=sharing)

Este documento incluye las siguientes secciones:
- Casos de prueba manuales.
- Capturas de pantalla que ilustran los resultados de las pruebas.
- Notas y observaciones sobre los escenarios de prueba realizados.
