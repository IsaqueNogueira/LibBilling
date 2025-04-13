# LibBilling - Documentação de Implementação

**LibBilling** é uma biblioteca desenvolvida para gerenciar assinaturas no Google Play Billing de forma simples e eficaz. Ela inclui funcionalidades como verificação de assinatura, abertura de tela de assinatura personalizada e gerenciamento do estado de usuário premium.

---

<img src="https://github.com/user-attachments/assets/5e9d44ef-8f2f-4191-84c3-a553f7c5bc0d" width="300">



## **Funcionalidades**

- Conexão com o Google Play Billing.
- Gerenciamento de assinaturas (compra e reconhecimento de assinaturas).
- Verificação do estado premium do usuário.
- Interface personalizável para exibir benefícios das assinaturas.

---

## **Como adicionar ao projeto**

1. **Adicione a dependência no arquivo `build.gradle`:**

   ```groovy
   implementation 'com.github.IsaqueNogueira:libbilling:Tag'
   ```

   use o
    ```groovy
     maven { url = uri("https://jitpack.io") }
    ```

3. **Sincronize o projeto** para baixar as dependências.

---

## **Uso**

### **1. Inicializar Koin**

Inicie o Koin no arquivo da Application do seu aplicativo para carregar o módulo de Billing. Adicione o seguinte código:
```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        loadBillingModule() // Carrega o módulo de Billing
    }
}
```

### **2. Verificar se o Usuário é Premium**

Use o seguinte método para verificar se o usuário possui uma assinatura ativa:

```kotlin
val isPremium = LibBilling.isUserPremium()
```

### **3. Abrir a Tela de Assinaturas**

Chame o método `openSubscriptionScreen` para exibir a tela de assinaturas personalizada:

#### Parâmetros:
- **activity**: Contexto da activity.
- **productIds**: Lista com os IDs dos produtos de assinatura (exatamente 3 IDs).
- **color** *(opcional)*: Cor personalizada da interface em hexadecimal Ex 0xFF1976D2.toInt().
- **listBenefit** *(opcional)*: Lista de benefícios associados aos produtos.

#### Exemplo de Uso:

```kotlin
LibBilling.openSubscriptionScreen(
    activity = this,
    productIds = listOf("product_1", "product_2", "product_3"),
    color = 0xFF1976D2.toInt(), // Azul personalizado
    listBenefit = listOf(
        Benefit(
            title = "Sem anúncios",
            description = "Elimine distrações e otimize sua experiência."
        ),
        Benefit(
            title = "Acesso Premium",
            description = "Conteúdo exclusivo para assinantes."
        )
    )
)
```

---

## **Exemplo Completo**

```kotlin
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificar estado do usuário
        if (LibBilling.isUserPremium()) {
            // Usuário é Premium
        } else {
            // Usuário não é Premium
        }

        // Abrir tela de assinatura
        findViewById<Button>(R.id.subscribeButton).setOnClickListener {
            LibBilling.openSubscriptionScreen(
                activity = this,
                productIds = listOf("product_1", "product_2", "product_3"),
                color = 0xFF1976D2.toInt(),
                listBenefit = listOf(
                    Benefit(
                        title = "Sem anúncios",
                        description = "Elimine distrações e otimize sua experiência."
                    )
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Iniciar conexão com o Billing Client
        LibBilling.startConnectionBillingClient(this)
    }

    override fun onStop() {
        super.onStop()
        // Encerrar conexão com o Billing Client
        LibBilling.endConnectionBillingClient()
    }
}
```

---

## **Observações**

- Certifique-se de configurar corretamente os IDs dos produtos no Google Play Console.
- Sempre inicialize a conexão com o Billing Client antes de realizar qualquer operação.
- A lista de `productIds` deve conter exatamente 3 itens.

---

