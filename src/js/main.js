//DECLARAÇÃO DE VARIAVEIS

// Slider de empresas
const titulo_empresa = document.getElementById("slider-titulo-empresa");
const icone_empresa = document.getElementById("slider-icone-empresa");
const div_um = document.getElementById("bloco-um");
const atual_empresa = document.getElementById("slider-atual-empresa");
const total_empresa = document.getElementById("slider-total-empresa");
const bolinhas_empresa = document.getElementById("slider-bolinhas-empresa");
const btn_avancar = document.getElementById("btn-avancar");
const btn_voltar = document.getElementById("btn-voltar");

// Slider de cooperativas
const titulo_coop = document.getElementById("slider-titulo-coop");
const icone_coop = document.getElementById("slider-icone-coop");
const div_um_coop = document.getElementById("bloco-um-coop");
const div_dois_coop = document.getElementById("bloco-dois-coop");
const div_tres_coop = document.getElementById("bloco-tres-coop");
const atual_coop = document.getElementById("slider-atual-coop");
const total_coop = document.getElementById("slider-total-coop");
const bolinhas_coop = document.getElementById("slider-bolinhas-coop");
const btn_avancar_coop = document.getElementById("btn-avancar-coop");
const btn_voltar_coop = document.getElementById("btn-voltar-coop");

let index = 0;
let index_coop = 0;

// LISTAS — EMPRESA (cada posição das listas é um passo)

const lista_titulo_empresa = [
  "Cadastre-se",
  "Procure cooperativas",
  "Analise",
  "Contate",
  "Registre",
  "Agende",
  "Aguarde",
];

const lista_icone_empresa = [
  "fa-right-to-bracket",
  "fa-map-location-dot",
  "fa-magnifying-glass",
  "fa-comments",
  "fa-file-pen",
  "fa-calendar-check",
  "fa-hourglass-half",
];

const lista_bloco_um = [
  "Comece a criar sua conta.",
  "Acesse o mapa de cooperativas.",
  "Acesse o menu de cooperativas salvas.",
  "Acesse o chat privado da cooperativa.",
  "Clique na opção “Criar nova coleta”, na página inicial.",
  "Clique em “Agendar”.",
  "Agora é só esperar a data da coleta.",
];

// LISTAS — COOPERATIVA

const lista_titulo_coop = [
  "Cadastre-se",
  "Se identifique",
  "Aguarde e converse",
  "Confirme",
];

const lista_icone_coop = [
  "fa-right-to-bracket",
  "fa-id-card",
  "fa-comments",
  "fa-circle-check",
];

const lista_bloco_um_coop = [
  "Comece a criar sua conta de cooperativa.",
  "Informe qual tipo de resíduo sua cooperativa é especializada.",
  "Espere que uma empresa escolha e contate sua cooperativa.",
  "Aguarde a empresa agendar a coleta.",
];

const lista_bloco_dois_coop = [
  "Preencha os campos com as informações pedidas.",
  "Essa informação fica visível para empresas, para que elas possam fazer as melhores escolhas para elas.",
  "Converse com a empresa sobre o agendamento da coleta.",
  "Confirme o agendamento.",
];

const lista_bloco_tres_coop = [
  "Aceite os Termos e Condições e conclua o cadastro.",
  "Adicione também fotos e a localização da sua cooperativa.",
  "Agende a coleta quando estiver tudo certo.",
  "Tudo pronto.",
];

//FUNÇÕES

// Cria uma bolinha (botão) para cada passo do slider
// container: div onde as bolinhas ficam
// titulos: lista de títulos (usada para a quantidade e para o aria-label)
// tipo: "empresa" ou "coop", para saber qual índice mudar no clique
function criar_bolinhas(container, titulos, tipo) {
  titulos.forEach((titulo, i) => {
    const bolinha = document.createElement("button");
    bolinha.className = "bolinha";
    bolinha.setAttribute("aria-label", `Ir para o passo ${i + 1}: ${titulo}`);

    bolinha.addEventListener("click", () => {
      if (tipo == "empresa") {
        index = i;
      } else {
        index_coop = i;
      }
      ajustar_slider();
    });

    container.appendChild(bolinha);
  });
}

// Marca a bolinha do passo atual
function marcar_bolinha(container, posicao) {
  container.querySelectorAll(".bolinha").forEach((bolinha, i) => {
    bolinha.setAttribute("aria-current", i == posicao ? "true" : "false");
  });
}

// Atualiza o conteúdo dos dois sliders
function ajustar_slider() {
  // Empresa
  titulo_empresa.innerText = lista_titulo_empresa[index];
  icone_empresa.className = `fa-solid ${lista_icone_empresa[index]}`;
  div_um.innerText = lista_bloco_um[index];
  atual_empresa.innerText = index + 1;
  marcar_bolinha(bolinhas_empresa, index);

  // Cooperativa
  titulo_coop.innerText = lista_titulo_coop[index_coop];
  icone_coop.className = `fa-solid ${lista_icone_coop[index_coop]}`;
  div_um_coop.innerText = lista_bloco_um_coop[index_coop];
  div_dois_coop.innerText = lista_bloco_dois_coop[index_coop];
  div_tres_coop.innerText = lista_bloco_tres_coop[index_coop];
  atual_coop.innerText = index_coop + 1;
  marcar_bolinha(bolinhas_coop, index_coop);
}

// Avança ou volta o slider
function mudar_slider(btn) {
  if (btn == "avancar") {
    index = (index + 1) % lista_titulo_empresa.length;
  } else if (btn == "voltar") {
    index =
      (index - 1 + lista_titulo_empresa.length) % lista_titulo_empresa.length;
  } else if (btn == "avancar_coop") {
    index_coop = (index_coop + 1) % lista_titulo_coop.length;
  } else if (btn == "voltar_coop") {
    index_coop =
      (index_coop - 1 + lista_titulo_coop.length) % lista_titulo_coop.length;
  }

  ajustar_slider();
}

//EVENTOS

total_empresa.innerText = lista_titulo_empresa.length;
total_coop.innerText = lista_titulo_coop.length;

criar_bolinhas(bolinhas_empresa, lista_titulo_empresa, "empresa");
criar_bolinhas(bolinhas_coop, lista_titulo_coop, "coop");

ajustar_slider();

btn_avancar.addEventListener("click", () => {
  mudar_slider("avancar");
});
btn_voltar.addEventListener("click", () => {
  mudar_slider("voltar");
});
btn_avancar_coop.addEventListener("click", () => {
  mudar_slider("avancar_coop");
});
btn_voltar_coop.addEventListener("click", () => {
  mudar_slider("voltar_coop");
});
