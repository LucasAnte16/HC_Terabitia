import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Quarto(
    val numero: Int,
    var ocupado: Boolean = false,
    var hospede: String? = null,
    var dias: Int = 0,
    var totalPago: Double = 0.0,
    var tipo: String? = null
)

data class Hospede(
    var nome: String,
    var idade: Int,
    val dataHoraCadastro: LocalDateTime = LocalDateTime.now()
)

data class EmpresaEmpresa(
    val nome: String,
    val custoGeral: Double
)

fun main() {
    val hotel = "Hotel Oceanview"
    val senhaCorreta = "2678"
    val maxTentativas = 3

    println("Bem-vindo ao $hotel")

    print("Digite o seu nome de usuário: ")
    val usuario = readlnOrNull()?.trim()?.ifEmpty { "Usuário" } ?: "Usuário"

    if (!realizarLogin(senhaCorreta, maxTentativas)) {
        println("Número de tentativas excedido. Sistema bloqueado!")
        return
    }

    println("Bem-vindo ao Hotel $hotel, $usuario. \nÉ um imenso prazer ter você por aqui!")

    val quartos = List(20) { Quarto(numero = it + 1) }
    val hospedesCadastrados = mutableListOf<Hospede>()

    while (true) {
        exibirMenuPrincipal()
        print("Escolha uma opção: ")

        when (readlnOrNull() ?: "") {
            "1" -> gerenciarReservas(quartos, usuario)
            "2" -> gerenciarCadastroHospedes(hospedesCadastrados)
            "3" -> gerenciarEventos(usuario)
            "4" -> gerenciarArCondicionado()
            "5" -> gerenciarAbastecimento(usuario)
            "6" -> gerarRelatoriosOperacionais(quartos, hospedesCadastrados)
            "7" -> {
                println("Muito obrigado e até logo, $usuario.")
                break
            }
            else -> tratarErroOpcaoInvalida()
        }
    }
}

// LOGIN E MENU PRINCIPAL

fun realizarLogin(senhaCorreta: String, maxTentativas: Int): Boolean {
    var tentativas = 0
    while (tentativas < maxTentativas) {
        print("Digite a sua senha: ")
        val senhaDigitada = readlnOrNull() ?: ""

        if (senhaDigitada == senhaCorreta) return true

        tentativas++
        val restantes = maxTentativas - tentativas
        if (restantes > 0) {
            println("Senha incorreta! Você ainda tem $restantes tentativa(s).")
        }
    }
    return false
}

fun exibirMenuPrincipal() {
    println("\n=== MENU PRINCIPAL ===")
    println("1. Reservas de Quartos")
    println("2. Cadastro de Hóspedes")
    println("3. Eventos")
    println("4. Ar-Condicionado")
    println("5. Abastecimento")
    println("6. Relatórios Operacionais")
    println("7. Sair")
    println("======================")
}

// MÓDULO 1: RESERVAS DE QUARTOS

fun gerenciarReservas(quartos: List<Quarto>, usuario: String) {
    while (true) {
        println("\n--- RESERVA DE QUARTOS ---")
        println("1. Listar Quartos (Status Detalhado)")
        println("2. Reservar Quarto")
        println("3. Exibir Mapa de Ocupação (Grade)")
        println("4. Finalizar Estadia (Checkout)")
        println("5. Voltar ao Menu Principal")
        print("Escolha uma opção: ")

        when (readlnOrNull() ?: "") {
            "1" -> listarQuartos(quartos)
            "2" -> realizarReserva(quartos, usuario)
            "3" -> exibirMapaGrade(quartos)
            "4" -> finalizarEstadia(quartos)
            "5" -> break
            else -> tratarErroOpcaoInvalida()
        }
    }
}

fun listarQuartos(quartos: List<Quarto>) {
    println("\n--- STATUS DOS QUARTOS ---")
    for (quarto in quartos) {
        val status = if (quarto.ocupado) "OCUPADO (Hóspede: ${quarto.hospede} | Tipo: ${quarto.tipo})" else "LIVRE"
        println("Quarto ${quarto.numero.toString().padStart(2, '0')}: $status")
    }
}

fun realizarReserva(quartos: List<Quarto>, usuario: String) {
    println("\n[Reservas]")

    print("Informe o valor da diária: ")
    val valorDiaria = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull()
    if (valorDiaria == null || valorDiaria <= 0.0) {
        println("Valor inválido, $usuario")
        return
    }

    print("Informe a quantidade de diárias (1-30): ")
    val dias = readlnOrNull()?.toIntOrNull()
    if (dias == null || dias !in 1..30) {
        println("Valor inválido, $usuario")
        return
    }

    print("Informe o nome do hóspede: ")
    val nomeHospede = readlnOrNull()?.trim() ?: ""
    if (nomeHospede.isEmpty()) {
        println("Valor inválido, $usuario")
        return
    }

    print("Tipo de quarto (S/E/L): ")
    val tipoQuarto = readlnOrNull()?.uppercase() ?: ""

    val (fator, nomeTipo) = when (tipoQuarto) {
        "S" -> Pair(1.00, "Standard")
        "E" -> Pair(1.35, "Executivo")
        "L" -> Pair(1.65, "Luxo")
        else -> {
            println("Valor inválido, $usuario")
            return
        }
    }

    var quartoEscolhido: Quarto? = null
    while (quartoEscolhido == null) {
        print("Escolha um quarto (1-20): ")
        val numeroQuarto = readlnOrNull()?.toIntOrNull()

        if (numeroQuarto == null || numeroQuarto !in 1..20) {
            println("Número de quarto inválido! Escolha de 1 a 20.")
            continue
        }

        val quarto = quartos[numeroQuarto - 1]
        if (quarto.ocupado) {
            println("⚠️ Quarto já está ocupado!")
            val livres = quartos.filter { !it.ocupado }.map { it.numero }
            println("Quartos livres disponíveis: $livres")
            println("Por favor, selecione uma das opções livres.")
        } else {
            quartoEscolhido = quarto
        }
    }

    val subtotal = valorDiaria * dias * fator
    val taxaServico = subtotal * 0.10
    val totalFinal = subtotal + taxaServico

    println("\nResumo:")
    println("Hóspede: $nomeHospede")
    println("Quarto: ${quartoEscolhido.numero} ($nomeTipo)")
    println("Subtotal: ${formatarMoeda(subtotal)}")
    println("Taxa de serviço (10%): ${formatarMoeda(taxaServico)}")
    println("Total: ${formatarMoeda(totalFinal)}")
    println()
    print("$usuario, confirma a reserva? (S/N): ")

    if (readlnOrNull()?.uppercase() == "S") {
        quartoEscolhido.ocupado = true
        quartoEscolhido.hospede = nomeHospede
        quartoEscolhido.dias = dias
        quartoEscolhido.totalPago = totalFinal
        quartoEscolhido.tipo = nomeTipo

        println("Reserva efetuada com sucesso.")
        exibirMapaGrade(quartos)
    } else {
        println("Reserva cancelada. Retornando ao menu principal.")
    }
}

fun exibirMapaGrade(quartos: List<Quarto>) {
    println("\n--- MAPA DE OCUPAÇÃO DOS QUARTOS (Grade 4x5) ---")
    println("       [C1]  [C2]  [C3]  [C4]  [C5]")

    for (linha in 0 until 4) {
        print("Fila ${linha + 1}: ")
        for (coluna in 0 until 5) {
            val index = linha * 5 + coluna
            val quarto = quartos[index]
            val statusChar = if (quarto.ocupado) "O" else "L"
            print("  $statusChar   ")
        }
        println()
    }
    println("-------------------------------------------------")
    println("Legenda: L = Livre | O = Ocupado")
}

fun finalizarEstadia(quartos: List<Quarto>) {
    println("\n--- FECHAMENTO DE CONTA (CHECKOUT) ---")
    print("Digite o número do quarto (1 a 20): ")
    val numeroQuarto = readlnOrNull()?.toIntOrNull()

    if (numeroQuarto == null || numeroQuarto !in 1..20) {
        println("Número de quarto inválido!")
        return
    }

    val quarto = quartos[numeroQuarto - 1]
    if (!quarto.ocupado) {
        println("O Quarto ${quarto.numero} já está livre.")
        return
    }

    println("\nDados da Conta:")
    println("Hóspede: ${quarto.hospede}")
    println("Quarto: ${quarto.numero} (${quarto.tipo})")
    println("Período: ${quarto.dias} diária(s)")
    println("Total Consolidado com Taxas: ${formatarMoeda(quarto.totalPago)}")
    print("Confirmar encerramento e liberação do quarto? (S/N): ")

    if (readlnOrNull()?.uppercase() == "S") {
        println("Conta fechada. Obrigado pela estadia, ${quarto.hospede}!")
        quarto.ocupado = false
        quarto.hospede = null
        quarto.dias = 0
        quarto.totalPago = 0.0
        quarto.tipo = null
    } else {
        println("Operação cancelada. O quarto permanece ocupado.")
    }
}

// MÓDULO 2: CADASTRO DE HÓSPEDES

fun gerenciarCadastroHospedes(hospedes: MutableList<Hospede>) {
    val capacidadeMaxima = 15

    while (true) {
        println("\n--- CADASTRO DE HÓSPEDES ---")
        println("1. Cadastrar")
        println("2. Pesquisar por nome exato")
        println("3. Pesquisar por prefixo")
        println("4. Listar ordenado (A-Z)")
        println("5. Atualizar cadastro")
        println("6. Remover cadastro")
        println("7. Voltar")
        print("Escolha uma opção: ")

        when (readlnOrNull()?.trim() ?: "") {
            "1" -> cadastrarHospede(hospedes, capacidadeMaxima)
            "2" -> pesquisarPorNomeExato(hospedes)
            "3" -> pesquisarPorPrefixo(hospedes)
            "4" -> listarHospedesOrdenados(hospedes)
            "5" -> atualizarHospede(hospedes)
            "6" -> removerHospede(hospedes)
            "7" -> break
            else -> tratarErroOpcaoInvalida()
        }
    }
}

fun cadastrarHospede(hospedes: MutableList<Hospede>, capacidadeMaxima: Int) {
    if (hospedes.size >= capacidadeMaxima) {
        println("Máximo de cadastros atingido")
        return
    }

    print("\nNome do hóspede: ")
    val nome = readlnOrNull()?.trim() ?: ""

    if (nome.isEmpty()) {
        println("Nome inválido.")
        return
    }

    val existe = hospedes.any { it.nome.equals(nome, ignoreCase = true) }
    if (existe) {
        println("Hóspede já cadastrado")
        return
    }

    print("Idade do hóspede: ")
    val idade = readlnOrNull()?.toIntOrNull()
    if (idade == null || idade < 0) {
        println("Idade inválida.")
        return
    }

    hospedes.add(Hospede(nome = nome, idade = idade))
    hospedes.sortBy { it.nome.lowercase() }
    println("Operação realizada com sucesso")
}

fun pesquisarPorNomeExato(hospedes: List<Hospede>) {
    print("\nDigite o nome exato do hóspede: ")
    val nomeBusca = readlnOrNull()?.trim() ?: ""

    val encontrado = hospedes.find { it.nome.equals(nomeBusca, ignoreCase = true) }
    if (encontrado != null) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        println("Hóspede encontrado: ${encontrado.nome} | Idade: ${encontrado.idade} | Cadastrado em: ${encontrado.dataHoraCadastro.format(formatter)}")
    } else {
        println("Hóspede não encontrado")
    }
}

fun pesquisarPorPrefixo(hospedes: List<Hospede>) {
    print("\nDigite o prefixo do nome: ")
    val prefixo = readlnOrNull()?.trim() ?: ""

    if (prefixo.isEmpty()) {
        println("Prefixo inválido.")
        return
    }

    val resultados = hospedes.filter { it.nome.startsWith(prefixo, ignoreCase = true) }

    if (resultados.isNotEmpty()) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        println("\n--- HÓSPEDES ENCONTRADOS ---")
        resultados.forEach { h ->
            println("- ${h.nome} | Idade: ${h.idade} | Data/Hora: ${h.dataHoraCadastro.format(formatter)}")
        }
    } else {
        println("Hóspede não encontrado")
    }
}

fun listarHospedesOrdenados(hospedes: MutableList<Hospede>): List<Hospede> {
    println("\n--- LISTA DE HÓSPEDES (A-Z) ---")
    if (hospedes.isEmpty()) {
        println("Nenhum hóspede cadastrado.")
        return emptyList()
    }

    hospedes.sortBy { it.nome.lowercase() }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    hospedes.forEachIndexed { index, h ->
        println("Índice [$index] | Nome: ${h.nome} | Idade: ${h.idade} | Data/Hora Cadastro: ${h.dataHoraCadastro.format(formatter)}")
    }

    return hospedes
}

fun atualizarHospede(hospedes: MutableList<Hospede>) {
    if (hospedes.isEmpty()) {
        println("Hóspede não encontrado")
        return
    }

    listarHospedesOrdenados(hospedes)

    print("\nInforme o índice do hóspede que deseja atualizar: ")
    val indice = readlnOrNull()?.toIntOrNull()

    if (indice == null || indice !in hospedes.indices) {
        println("Hóspede não encontrado")
        return
    }

    val hospedeAtual = hospedes[indice]

    print("Novo nome (pressione ENTER para manter '${hospedeAtual.nome}'): ")
    val novoNome = readlnOrNull()?.trim() ?: ""

    if (novoNome.isNotEmpty() && !novoNome.equals(hospedeAtual.nome, ignoreCase = true)) {
        val duplicado = hospedes.any { it.nome.equals(novoNome, ignoreCase = true) }
        if (duplicado) {
            println("Hóspede já cadastrado")
            return
        }
        hospedeAtual.nome = novoNome
    }

    print("Nova idade (pressione ENTER para manter '${hospedeAtual.idade}'): ")
    val novaIdadeStr = readlnOrNull()?.trim() ?: ""
    if (novaIdadeStr.isNotEmpty()) {
        val novaIdade = novaIdadeStr.toIntOrNull()
        if (novaIdade != null && novaIdade >= 0) {
            hospedeAtual.idade = novaIdade
        } else {
            println("Idade inválida. Mantendo a idade anterior.")
        }
    }

    hospedes.sortBy { it.nome.lowercase() }
    println("Operação realizada com sucesso")
}

fun removerHospede(hospedes: MutableList<Hospede>) {
    if (hospedes.isEmpty()) {
        println("Hóspede não encontrado")
        return
    }

    listarHospedesOrdenados(hospedes)

    print("\nInforme o índice do hóspede que deseja remover: ")
    val indice = readlnOrNull()?.toIntOrNull()

    if (indice == null || indice !in hospedes.indices) {
        println("Hóspede não encontrado")
        return
    }

    hospedes.removeAt(indice)
    println("Operação realizada com sucesso")
}

// MÓDULO 3: EVENTOS (Espaço reservado)

data class ReservaEvento(
    val auditorio: String,
    val empresa: String,
    val diaSemana: String,
    val horaInicio: Int,
    val horaFim: Int,
    val convidados: Int,
    val duracao: Int,
    val cadeirasExtras: Int,
    val garcons: Int,
    val custoGarcons: Double,
    val custoBuffet: Double,
    val custoTotal: Double
)

fun gerenciarEventos(usuario: String) {
    val reservasCadastradas = mutableListOf<ReservaEvento>()

    while (true) {
        println("\n--- ORÇAMENTO E RESERVA DE EVENTOS ---")
        println("1. Nova Reserva de Evento (Fluxo Completo)")
        println("2. Listar Reservas Efetuadas")
        println("3. Voltar ao Menu Principal")
        print("Escolha uma opção: ")

        when (readlnOrNull()?.trim() ?: "") {
            "1" -> processarFluxoCompletoEvento(reservasCadastradas, usuario)
            "2" -> listarReservasEventos(reservasCadastradas)
            "3" -> break
            else -> tratarErroOpcaoInvalida()
        }
    }
}

fun processarFluxoCompletoEvento(reservas: MutableList<ReservaEvento>, usuario: String) {
    println("\n=== NOVO ORÇAMENTO DE EVENTO ===")

    // PARTE A: CAPACIDADE E SELEÇÃO DE AUDITÓRIO

    print("Qual é o número de convidados do evento? ")
    val convidados = readlnOrNull()?.toIntOrNull()

    if (convidados == null || convidados <= 0 || convidados > 350) {
        println("Número de convidados inválido.")
        return
    }

    val auditorio: String
    val cadeirasExtras: Int

    if (convidados <= 220) {
        auditorio = "Auditório Laranja"
        cadeirasExtras = if (convidados > 150) convidados - 150 else 0
        println("Auditório recomendado: $auditorio")
        if (cadeirasExtras > 0) {
            println("Será necessário incluir $cadeirasExtras cadeira(s) adicional(is).")
        }
    } else {
        auditorio = "Auditório Colorado"
        cadeirasExtras = 0
        println("Auditório recomendado: $auditorio")
    }

    // PARTE B: AGENDA E DISPONIBILIDADE

    println("\n--- Agendamento do Evento ---")
    print("Qual o dia da semana do evento (ex: segunda, sabado, domingo)? ")
    val diaInput = readlnOrNull()?.trim()?.lowercase() ?: ""
    val diaSemana = removerAcentos(diaInput)

    val (horaAbertura, horaFechamento) = when (diaSemana) {
        "segunda", "segunda-feira", "terca", "terca-feira", "quarta", "quarta-feira",
        "quinta", "quinta-feira", "sexta", "sexta-feira" -> Pair(7, 23)
        "sabado", "domingo" -> Pair(7, 15)
        else -> {
            println("Dia da semana inválido.")
            return
        }
    }

    print("Qual a hora inicial do evento (0-23h)? ")
    val horaInicio = readlnOrNull()?.toIntOrNull()
    if (horaInicio == null || horaInicio < horaAbertura || horaInicio >= horaFechamento) {
        println("Auditório indisponível. Horário de funcionamento no dia ($diaSemana): ${horaAbertura}hs às ${horaFechamento}hs.")
        return
    }

    print("Qual a duração do evento em horas (1 a 12h)? ")
    val duracao = readlnOrNull()?.toIntOrNull()
    if (duracao == null || duracao !in 1..12) {
        println("Duração inválida! O evento deve durar entre 1 e 12 horas.")
        return
    }

    val horaFim = horaInicio + duracao
    if (horaFim > horaFechamento) {
        println("Auditório indisponível. O evento termina às ${horaFim}hs, mas o horário limite no dia ($diaSemana) é às ${horaFechamento}hs.")
        return
    }

    print("Qual o nome da empresa contratante? ")
    val empresa = readlnOrNull()?.trim() ?: ""
    if (empresa.isEmpty()) {
        println("Nome da empresa inválido.")
        return
    }

    println("Auditório disponível para reserva!")

    // PARTE C: EQUIPE DE GARÇONS

    val garconsBase = Math.ceil(convidados / 12.0).toInt()
    val garconsReforco = duracao / 2 // Equivalente a floor(duracao / 2)
    val totalGarcons = garconsBase + garconsReforco
    val custoGarcons = totalGarcons * duracao * 10.50

    // PARTE D: BUFFET

    val litrosCafe = convidados * 0.2
    val litrosAgua = convidados * 0.5
    val qtdSalgados = convidados * 7

    val custoCafe = litrosCafe * 0.80
    val custoAgua = litrosAgua * 0.40
    val custoSalgados = (qtdSalgados / 100.0) * 34.00
    val custoBuffet = custoCafe + custoAgua + custoSalgados

    val custoTotalGeral = custoGarcons + custoBuffet


    // PARTE E: RELATÓRIO E DECISÃO

    println("\n==================================================")
    println("           RELATÓRIO TÉCNICO DO EVENTO            ")
    println("==================================================")
    println("Empresa Contratante: $empresa")
    println("Local: $auditorio")
    println("Data/Horário: $diaSemana, das ${horaInicio}hs às ${horaFim}hs (Duração: ${duracao}h)")
    println("Convidados: $convidados")
    if (cadeirasExtras > 0) {
        println("Cadeiras Adicionais Necessárias: $cadeirasExtras")
    }
    println("\n--- RECURSOS E CUSTOS ---")
    println("Garçons Necessários: $totalGarcons (Base: $garconsBase | Reforço Duração: $garconsReforco)")
    println("  └─ Custo Garçons: ${formatarMoeda(custoGarcons)}")
    println("Buffet Previsto:")
    println("  ├─ Café: ${String.format("%.1f", litrosCafe)}L (${formatarMoeda(custoCafe)})")
    println("  ├─ Água: ${String.format("%.1f", litrosAgua)}L (${formatarMoeda(custoAgua)})")
    println("  └─ Salgados: $qtdSalgados un (${formatarMoeda(custoSalgados)})")
    println("  └─ Custo Total Buffet: ${formatarMoeda(custoBuffet)}")
    println("--------------------------------------------------")
    println("VALOR TOTAL DO EVENTO: ${formatarMoeda(custoTotalGeral)}")
    println("==================================================")

    print("\n$usuario, confirma a reserva do evento para $empresa? (S/N): ")
    val confirmacao = readlnOrNull()?.trim()?.uppercase() ?: ""

    if (confirmacao == "S") {
        val novaReserva = ReservaEvento(
            auditorio = auditorio,
            empresa = empresa,
            diaSemana = diaSemana,
            horaInicio = horaInicio,
            horaFim = horaFim,
            convidados = convidados,
            duracao = duracao,
            cadeirasExtras = cadeirasExtras,
            garcons = totalGarcons,
            custoGarcons = custoGarcons,
            custoBuffet = custoBuffet,
            custoTotal = custoTotalGeral
        )
        reservas.add(novaReserva)
        println("\nAuditório reservado para $empresa: $diaSemana às ${horaInicio}hs. Reserva efetuada com sucesso!")
    } else {
        println("\nReserva não efetuada.")
    }
}

fun listarReservasEventos(reservas: List<ReservaEvento>) {
    println("\n--- RESERVAS DE EVENTOS EFETUADAS ---")
    if (reservas.isEmpty()) {
        println("Nenhuma reserva de evento cadastrada.")
        return
    }

    reservas.forEachIndexed { index, r ->
        println("\n[Reserva #${index + 1}]")
        println("Empresa: ${r.empresa} | Auditório: ${r.auditorio}")
        println("Agenda: ${r.diaSemana}, das ${r.horaInicio}h às ${r.horaFim}h (${r.duracao}h)")
        println("Convidados: ${r.convidados} | Garçons: ${r.garcons}")
        println("Valor Total: ${formatarMoeda(r.custoTotal)}")
    }
}

fun removerAcentos(texto: String): String {
    val comAcento = "áàâãäéèêëíìîïóòôõöúùûüç"
    val semAcento = "aaaaaeeeeiiiiooooouuuuc"
    var resultado = texto
    for (i in comAcento.indices) {
        resultado = resultado.replace(comAcento[i], semAcento[i])
    }
    return resultado
}

// MÓDULO 4: AR-CONDICIONADO

fun gerenciarArCondicionado() {
    println("\n--- COTAÇÃO DE MANUTENÇÃO DE AR-CONDICIONADO ---")

    val empresas = mutableListOf<EmpresaEmpresa>()

    while (true) {
        print("\nQual o nome da empresa de manutenção? ")
        val nomeEmpresa = readlnOrNull()?.trim() ?: ""
        if (nomeEmpresa.isEmpty()) break

        print("Qual o valor do serviço por aparelho (R$)? ")
        val valorAparelho = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

        print("Qual a quantidade de aparelhos? ")
        val qtdAparelhos = readlnOrNull()?.toIntOrNull() ?: 0

        print("Qual o percentual de desconto concedido (%)? ")
        val pctDesconto = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

        print("Qual o número mínimo de aparelhos para obter o desconto? ")
        val minAparelhosDesconto = readlnOrNull()?.toIntOrNull() ?: 0

        var valorBruto = valorAparelho * qtdAparelhos
        if (qtdAparelhos >= minAparelhosDesconto && pctDesconto > 0.0) {
            valorBruto -= valorBruto * (pctDesconto / 100.0)
        }

        empresas.add(EmpresaEmpresa(nomeEmpresa, valorBruto))
        println("O serviço da empresa $nomeEmpresa custará ${formatarMoeda(valorBruto)}")

        print("\nDeseja informar os dados de outra empresa? (S/N): ")
        if (!readlnOrNull()?.uppercase().equals("S")) break
    }

    if (empresas.isNotEmpty()) {
        val menorOrcamento = empresas.minByOrNull { it.custoGeral }!!
        println("\nO orçamento mais vantajoso é o da empresa ${menorOrcamento.nome} por ${formatarMoeda(menorOrcamento.custoGeral)}")
    } else {
        println("Nenhuma empresa foi cadastrada para cotação.")
    }
}

// MÓDULO 5: ABASTECIMENTO DE COMBUSTÍVEL

fun gerenciarAbastecimento(usuario: String) {
    println("\n[Abastecimento]")

    print("Informe o preço do álcool no posto Wayne Oil: ")
    val alcWayne = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

    print("Informe o preço da gasolina no posto Wayne Oil: ")
    val gasWayne = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

    print("Informe o preço do álcool no posto Stark Petrol: ")
    val alcStark = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

    print("Informe o preço da gasolina no posto Stark Petrol: ")
    val gasStark = readlnOrNull()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

    if (alcWayne <= 0 || gasWayne <= 0 || alcStark <= 0 || gasStark <= 0) {
        println("Erro: Todos os preços devem ser informados com valores válidos maiores que zero.")
        return
    }

    val capacidadeTanque = 42.0

    val combWayne = if (alcWayne <= gasWayne * 0.70) "Álcool" else "Gasolina"
    val precoLitroWayne = if (combWayne == "Álcool") alcWayne else gasWayne
    val totalWayne = precoLitroWayne * capacidadeTanque

    val combStark = if (alcStark <= gasStark * 0.70) "Álcool" else "Gasolina"
    val precoLitroStark = if (combStark == "Álcool") alcStark else gasStark
    val totalStark = precoLitroStark * capacidadeTanque

    println()
    println("Wayne Oil: melhor opção = $combWayne | Total (42L) = ${formatarMoeda(totalWayne)}")
    println("Stark Petrol: melhor opção = $combStark | Total (42L) = ${formatarMoeda(totalStark)}")
    println()

    if (totalWayne <= totalStark) {
        println("$usuario, é mais barato abastecer com ${combWayne.lowercase()} no posto Wayne Oil.")
    } else {
        println("$usuario, é mais barato abastecer com ${combStark.lowercase()} no posto Stark Petrol.")
    }
}

// MÓDULO 6: RELATÓRIOS OPERACIONAIS

fun gerarRelatoriosOperacionais(quartos: List<Quarto>, hospedes: List<Hospede>) {
    println("\n=== RELATÓRIO OPERACIONAL CONSOLIDADO ===")

    val ocupados = quartos.filter { it.ocupado }
    val arrecadacaoTotal = ocupados.sumOf { it.totalPago }

    println("1. OCUPAÇÃO DOS QUARTOS:")
    println("   - Total de Quartos: ${quartos.size}")
    println("   - Quartos Ocupados: ${ocupados.size}")
    println("   - Quartos Livres: ${quartos.size - ocupados.size}")
    println("   - Taxa de Ocupação: ${String.format("%.1f", (ocupados.size.toDouble() / quartos.size) * 100)}%")

    println("\n2. DEMONSTRATIVO FINANCEIRO:")
    println("   - Receita Consolidada Prevista (Em Estadia): ${formatarMoeda(arrecadacaoTotal)}")

    println("\n3. HÓSPEDES CADASTRADOS NO SISTEMA:")
    println("   - Total de registros na base: ${hospedes.size}")

    println("=========================================")
}

// UTILITÁRIOS

fun formatarMoeda(valor: Double): String {
    val ptBr = Locale("pt", "BR")
    return NumberFormat.getCurrencyInstance(ptBr).format(valor)
}

fun tratarErroOpcaoInvalida() {
    println("\n[ERRO] Opção inválida! Selecione uma opção válida do menu.")
}