package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.ArquivoCliente;
import models.BufferDeClientes;
import models.Cliente;
import utils.OrdenarCliente;

public class ClienteGUI2 extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private final BufferDeClientes bufferDeClientes;
    private final int TAMANHO_BUFFER = 10000;
    private int registrosCarregados = 0; // Contador de registros já carregados
    private String arquivoSelecionado;
    private boolean arquivoCarregado = false; // Para verificar se o arquivo foi carregado
    private final List<Cliente> listaClientes;

    public ClienteGUI2() {
        setTitle("Gerenciamento de Clientes");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        bufferDeClientes = new BufferDeClientes();
        listaClientes = new ArrayList<>();
        criarInterface();
    }

    private void carregarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int retorno = fileChooser.showOpenDialog(this);

        if (retorno == JFileChooser.APPROVE_OPTION) {
            arquivoSelecionado = fileChooser.getSelectedFile().getAbsolutePath();
            bufferDeClientes.associaBuffer(new ArquivoCliente()); // Substitua por sua implementação
            bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado); // Passa o nome do arquivo aqui
            registrosCarregados = 0; // Reseta o contador
            tableModel.setRowCount(0); // Limpa a tabela
            carregarMaisClientes(); // Carrega os primeiros clientes
            arquivoCarregado = true; // Marca que o arquivo foi carregado
        }
    }

    private JTextField campoPesquisa; // Campo de pesquisa para "Pesquisar Cliente"

    private void criarInterface() {
        JPanel panel = new JPanel(new BorderLayout());

        // Painel de botões na parte inferior
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnCarregarCliente = new JButton("Carregar Clientes");
        JButton btnPesquisarCliente = new JButton("Pesquisar");
        JButton btnInserirCliente = new JButton("Inserir");
        JButton btnRemoverCliente = new JButton("Remover");
        JButton btnOrdenarClientes = new JButton("Ordenar Clientes");

        // Inicialmente, só o botão de carregar é visível
        btnPesquisarCliente.setVisible(false);
        btnInserirCliente.setVisible(false);
        btnRemoverCliente.setVisible(false);
        btnOrdenarClientes.setVisible(false);

        // Tabela para mostrar os clientes
        tableModel = new DefaultTableModel(new String[]{"#", "Nome", "Sobrenome", "Telefone", "Endereço", "Credit Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Listener para rolagem (carregar mais clientes ao rolar)
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!scrollPane.getVerticalScrollBar().getValueIsAdjusting()) {
                    // Verifica se estamos no final da tabela e se o arquivo foi carregado
                    if (arquivoCarregado &&
                            scrollPane.getVerticalScrollBar().getValue() +
                                    scrollPane.getVerticalScrollBar().getVisibleAmount() >=
                                    scrollPane.getVerticalScrollBar().getMaximum()) {
                        carregarMaisClientes();
                    }
                }
            }
        });

        // Ação para carregar clientes
        btnCarregarCliente.addActionListener(e -> {
            carregarArquivo();

            // Mostrar os outros botões após carregar o arquivo
            btnPesquisarCliente.setVisible(true);
            btnInserirCliente.setVisible(true);
            btnRemoverCliente.setVisible(true);
            btnOrdenarClientes.setVisible(true);
        });

        // Ação para buscar cliente
        btnPesquisarCliente.addActionListener(e -> {
            if (arquivoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Nenhum arquivo selecionado");
                return;
            }
            new PesquisarCliente(bufferDeClientes, tableModel, arquivoSelecionado);
        });

        // Ação para inserir novo cliente
        btnInserirCliente.addActionListener(e -> new InserirCliente(listaClientes, tableModel, bufferDeClientes));

        // Ação para remover cliente
        btnRemoverCliente.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para remover.");
            }
        });

        // Ação para ordenar clientes
        btnOrdenarClientes.addActionListener( e -> ordenarAlfabeticamente());

        // Adicionando botões ao painel
        btnPanel.add(btnCarregarCliente);
        btnPanel.add(btnInserirCliente);
        btnPanel.add(btnRemoverCliente);
        btnPanel.add(btnOrdenarClientes);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void carregarMaisClientes() {
        // Carrega apenas 10.000 registros de cada vez
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER); // Chama o método com o tamanho do buffer
        if (clientes != null && clientes.length > 0) {
            for (Cliente cliente : clientes) {
                if (cliente != null) { // Verifica se o cliente não é nulo
                    tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, cliente.getNome(), cliente.getSobrenome(), cliente.getTelefone(), cliente.getEndereco(), cliente.getCreditScore()});
                }
            }
            registrosCarregados += clientes.length; // Atualiza o contador
        }
    }

    private void ordenarAlfabeticamente() {
        if (!arquivoCarregado) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo foi carregado.");
            return;
        }

        // Define output file name
        String outputFile = arquivoSelecionado + "_ordenado.dat";

        try {
            // Call OrdenarCliente to sort the file
            OrdenarCliente ordenarCliente = new OrdenarCliente();
            ordenarCliente.sort(arquivoSelecionado, outputFile);

            JOptionPane.showMessageDialog(this, "Clientes ordenados com sucesso!");

            // Clear the table before reloading the sorted data
            tableModel.setRowCount(0);

            // Reload the sorted file into the table
            bufferDeClientes.inicializaBuffer("leitura", outputFile);
            carregarMaisClientes();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao ordenar clientes: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteGUI2 gui = new ClienteGUI2();
            gui.setVisible(true);
        });
    }
}