package com.sistema.eventos.view;

import com.sistema.eventos.model.CategoriaEvento;
import com.sistema.eventos.model.Evento;
import com.sistema.eventos.model.Participacao;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.repository.Repositorio;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class SistemaEventos {
    private Repositorio<Usuario> usuarios = new Repositorio<>();
    private Repositorio<Evento> eventos = new Repositorio<>();
    private Repositorio<Participacao> participacoes = new Repositorio<>();
    private Usuario usuarioLogado = null;
    private final String arquivoEventos = "events.data";
    private int proximoIdUsuario = 1;
    private int proximoIdEvento = 1;

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public SistemaEventos() {
        // Carregar eventos do arquivo ao iniciar
        carregarEventos();

        // Adicionar alguns usuários de exemplo
        usuarios.add(new Usuario(proximoIdUsuario++, "Admin", "admin@email.com",
                "999999999", LocalDate.of(1990, 1, 1), "São Paulo"));
    }

    // Carregar eventos do arquivo
    private void carregarEventos() {
        File arquivo = new File(arquivoEventos);
        if (!arquivo.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    Evento evento = Evento.fromFileString(linha);
                    eventos.add(evento);
                    if (evento.getId() >= proximoIdEvento) {
                        proximoIdEvento = evento.getId() + 1;
                    }
                }
            }
            System.out.println("Eventos carregados: " + eventos.lista().size());
        } catch (IOException e) {
            System.out.println("Erro ao carregar eventos: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao processar arquivo de eventos: " + e.getMessage());
        }
    }

    // Salvar eventos no arquivo
    private void salvarEventos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoEventos))) {
            for (Evento evento : eventos.lista()) {
                writer.write(evento.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    // Cadastrar novo usuário
    public void cadastrarUsuario() {
        limparTela();
        System.out.println("=== CADASTRO DE USUÁRIO ===\n");

        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Data de nascimento (dd/mm/aaaa): ");
        LocalDate dataNascimento = null;
        while (dataNascimento == null) {
            try {
                dataNascimento = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.print("Data inválida. Digite novamente (dd/mm/aaaa): ");
            }
        }

        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();

        Usuario usuario = new Usuario(proximoIdUsuario++, nome, email, telefone,
                dataNascimento, cidade);
        usuarios.add(usuario);
        System.out.println("\nUsuário cadastrado com sucesso!");
    }

    // Login do usuário
    public boolean login() {
        limparTela();
        System.out.println("=== LOGIN ===\n");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        Optional<Usuario> usuario = usuarios.lista().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (usuario.isPresent()) {
            usuarioLogado = usuario.get();
            System.out.printf("\nBem-vindo, %s!\n", usuarioLogado.getNome());
            return true;
        } else {
            System.out.println("\nUsuário não encontrado!");
            return false;
        }
    }

    // Cadastrar novo evento
    public void cadastrarEvento() {
        limparTela();
        System.out.println("=== CADASTRO DE EVENTO ===\n");

        System.out.print("Nome do evento: ");
        String nome = scanner.nextLine();

        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();

        System.out.println("\nCategorias disponíveis:");
        for (CategoriaEvento cat : CategoriaEvento.values()) {
            System.out.printf("%d - %s\n", cat.ordinal(), cat.getDescricao());
        }

        System.out.print("Escolha o número da categoria: ");
        int catEscolha = Integer.parseInt(scanner.nextLine());
        CategoriaEvento categoria = CategoriaEvento.fromInt(catEscolha);

        System.out.print("Data e hora do evento (dd/mm/aaaa hh:mm): ");
        LocalDateTime horario = null;
        while (horario == null) {
            try {
                horario = LocalDateTime.parse(scanner.nextLine(), DATETIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.print("Data/hora inválida. Digite novamente: ");
            }
        }

        System.out.print("Descrição do evento: ");
        String descricao = scanner.nextLine();

        Evento evento = new Evento(proximoIdEvento++, nome, endereco, categoria,
                horario, descricao);
        eventos.add(evento);
        salvarEventos();
        System.out.println("\nEvento cadastrado com sucesso!");
    }

    // Listar todos os eventos
    public void listarEventos() {
        limparTela();
        System.out.println("=== TODOS OS EVENTOS ===\n");

        if (eventos.lista().isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }

        List<Evento> eventosOrdenados = eventos.lista().stream()
                .sorted(Comparator.comparing(Evento::getHorario))
                .toList();

        for (Evento evento : eventosOrdenados) {
            System.out.println(evento);
            System.out.printf("  Endereço: %s\n", evento.getEndereco());
            System.out.printf("  Descrição: %s\n", evento.getDescricao());
            long participantes = participacoes.lista().stream()
                    .filter(p -> p.getEventoId() == evento.getId())
                    .count();
            System.out.printf("  Participantes: %d\n", participantes);
            System.out.println();
        }

        if (usuarioLogado != null) {
            System.out.print("\nDeseja participar de algum evento? (s/n): ");
            if (scanner.nextLine().toLowerCase().startsWith("s")) {
                participarEvento();
            }
        }
    }

    // Participar de um evento
    public void participarEvento() {
        if (usuarioLogado == null) {
            System.out.println("Você precisa estar logado para participar de eventos!");
            return;
        }

        System.out.print("Digite o ID do evento que deseja participar: ");
        try {
            int eventoId = Integer.parseInt(scanner.nextLine());
            Optional<Evento> eventoOpt = eventos.lista().stream()
                    .filter(e -> e.getId() == eventoId)
                    .findFirst();

            if (eventoOpt.isPresent()) {
                // Verificar se já participa
                boolean jaParticipa = participacoes.lista().stream()
                        .anyMatch(p -> p.getUsuarioId() == usuarioLogado.getId()
                                && p.getEventoId() == eventoId);

                if (jaParticipa) {
                    System.out.println("Você já está participando deste evento!");
                } else {
                    participacoes.add(new Participacao(usuarioLogado.getId(),
                            eventoId, LocalDateTime.now()));
                    System.out.printf("Presença confirmada no evento: %s\n",
                            eventoOpt.get().getNome());
                }
            } else {
                System.out.println("Evento não encontrado!");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido!");
        }
    }

    // Visualizar eventos do usuário
    public void visualizarMeusEventos() {
        if (usuarioLogado == null) {
            System.out.println("Você precisa estar logado!");
            return;
        }

        limparTela();
        System.out.printf("=== MEUS EVENTOS (%s) ===\n\n", usuarioLogado.getNome());

        List<Evento> meusEventos = participacoes.lista().stream()
                .filter(p -> p.getUsuarioId() == usuarioLogado.getId())
                .map(p -> eventos.lista().stream()
                        .filter(e -> e.getId() == p.getEventoId())
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Evento::getHorario))
                .toList();

        if (meusEventos.isEmpty()) {
            System.out.println("Você não está participando de nenhum evento.");
        } else {
            for (Evento evento : meusEventos) {
                System.out.println(evento);
                Participacao part = participacoes.lista().stream()
                        .filter(p -> p.getEventoId() == evento.getId())
                        .findFirst()
                        .get();
                System.out.printf("  Data confirmação: %s\n",
                        part.getDataConfirmacao().format(DATETIME_FORMATTER));
                System.out.println();
            }

            System.out.print("\nDeseja cancelar participação em algum evento? (s/n): ");
            if (scanner.nextLine().toLowerCase().startsWith("s")) {
                cancelarParticipacao();
            }
        }
    }

    // Cancelar participação
    public void cancelarParticipacao() {
        System.out.print("Digite o ID do evento que deseja cancelar: ");
        try {
            int eventoId = Integer.parseInt(scanner.nextLine());
            Optional<Participacao> participacao = participacoes.lista().stream()
                    .filter(p -> p.getUsuarioId() == usuarioLogado.getId()
                            && p.getEventoId() == eventoId)
                    .findFirst();

            if (participacao.isPresent()) {
                participacoes.remover(participacao.get());
                System.out.println("Participação cancelada com sucesso!");
            } else {
                System.out.println("Você não está participando deste evento!");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido!");
        }
    }

    // Verificar eventos que estão acontecendo agora
    public void verificarEventosAgora() {
        limparTela();
        System.out.println("=== EVENTOS ACONTECENDO AGORA ===\n");

        List<Evento> eventosAgora = eventos.lista().stream()
                .filter(Evento::estaAcontecendoAgora)
                .toList();

        if (eventosAgora.isEmpty()) {
            System.out.println("Nenhum evento acontecendo no momento.");
        } else {
            eventosAgora.forEach(evento -> {
                System.out.printf("%s - %s (até %s)\n",
                        evento.getNome(),
                        evento.getEndereco(),
                        evento.getHorario().plusHours(2).format(
                                DateTimeFormatter.ofPattern("HH:mm")));
            });
        }
    }

    // Listar eventos passados
    public void listarEventosPassados() {
        limparTela();
        System.out.println("=== EVENTOS JÁ OCORRIDOS ===\n");

        List<Evento> eventosPassados = eventos.lista().stream()
                .filter(Evento::jaAconteceu)
                .sorted(Comparator.comparing(Evento::getHorario).reversed())
                .toList();

        if (eventosPassados.isEmpty()) {
            System.out.println("Nenhum evento passado encontrado.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            eventosPassados.forEach(evento -> {
                System.out.printf("%s - %s\n",
                        evento.getNome(),
                        evento.getHorario().format(formatter));
            });
        }
    }

    private void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Menu principal
    public void menuPrincipal() {
        int opcao;
        do {
            limparTela();
            System.out.println("=== SISTEMA DE EVENTOS ===\n");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Login");
            System.out.println("3 - Cadastrar evento");
            System.out.println("4 - Listar todos os eventos");
            System.out.println("5 - Meus eventos");
            System.out.println("6 - Eventos acontecendo agora");
            System.out.println("7 - Eventos passados");
            System.out.println("0 - Sair");
            System.out.printf("\nUsuário: %s\n",
                    usuarioLogado != null ? usuarioLogado.getNome() : "Não logado");
            System.out.print("\nEscolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1 -> cadastrarUsuario();
                    case 2 -> login();
                    case 3 -> cadastrarEvento();
                    case 4 -> listarEventos();
                    case 5 -> visualizarMeusEventos();
                    case 6 -> verificarEventosAgora();
                    case 7 -> listarEventosPassados();
                    case 0 -> System.out.println("\nSaindo...");
                    default -> System.out.println("Opção inválida!");
                }

                if (opcao != 0) {
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
                opcao = -1;
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine();
            }
        } while (opcao != 0);
    }

    // Ponto de entrada
    public static void main(String[] args) {
        SistemaEventos sistema = new SistemaEventos();
        sistema.menuPrincipal();
        scanner.close();
    }
}