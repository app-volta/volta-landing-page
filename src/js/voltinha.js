(function () {
  const C = { side: 0x58a34e, limb: 0x63b057, stripe: 0xa6e09d, outline: 0x3d7f35 };
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  const stage = document.getElementById('voltinha-stage');
  const fallback = document.getElementById('voltinha-fallback');
  if (!stage) return;
  // sem 3D (navegador antigo ou a biblioteca não carregou): fica a versão 2D desenhada
  function no3D() { stage.classList.add('sem-3d'); }
  if (typeof THREE === 'undefined') { no3D(); return; }
  let renderer;
  try { renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true }); }
  catch (err) { no3D(); return; }
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  stage.appendChild(renderer.domElement);

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(35, 1, 0.1, 100);
  scene.add(new THREE.HemisphereLight(0xffffff, 0x2a3a2c, 0.45));
  const key = new THREE.DirectionalLight(0xffffff, 0.6);
  key.position.set(3, 5, 9); scene.add(key);
  const rim = new THREE.DirectionalLight(0xc8ffbf, 0.3);
  rim.position.set(-6, 3, -5); scene.add(rim);

  const grad = new THREE.DataTexture(new Uint8Array([150,150,150,255, 212,212,212,255, 255,255,255,255]), 3, 1, THREE.RGBAFormat);
  grad.minFilter = grad.magFilter = THREE.NearestFilter; grad.needsUpdate = true;
  const toon = (color, extra) => new THREE.MeshToonMaterial(Object.assign({ color, gradientMap: grad }, extra || {}));

  // contorno de desenho (casca invertida empurrada pelas normais suavizadas)
  function smoothNormals(geo) {
    const pos = geo.attributes.position, nor = geo.attributes.normal;
    const map = new Map(), out = new Float32Array(pos.count * 3);
    const keyOf = i => [pos.getX(i), pos.getY(i), pos.getZ(i)].map(v => Math.round(v * 1000)).join(',');
    for (let i = 0; i < pos.count; i++) {
      const k = keyOf(i); const a = map.get(k) || [0, 0, 0];
      a[0] += nor.getX(i); a[1] += nor.getY(i); a[2] += nor.getZ(i); map.set(k, a);
    }
    for (let i = 0; i < pos.count; i++) {
      const a = map.get(keyOf(i)); const l = Math.hypot(a[0], a[1], a[2]) || 1;
      out[i * 3] = a[0] / l; out[i * 3 + 1] = a[1] / l; out[i * 3 + 2] = a[2] / l;
    }
    geo.setAttribute('snormal', new THREE.BufferAttribute(out, 3));
  }
  function outlineMat(width) {
    const m = new THREE.MeshBasicMaterial({ color: C.outline, side: THREE.BackSide });
    m.onBeforeCompile = sh => {
      sh.vertexShader = 'attribute vec3 snormal;\n' + sh.vertexShader.replace('#include <begin_vertex>', 'vec3 transformed = position + snormal * ' + width.toFixed(3) + ';');
    };
    return m;
  }
  function withOutline(mesh, width) {
    smoothNormals(mesh.geometry);
    const o = new THREE.Mesh(mesh.geometry, outlineMat(width));
    o.raycast = () => {};
    mesh.add(o);
    return mesh;
  }

  // ---------- forma da seta ----------
  function roundedPath(ctx, pts, radii, tx, ty) {
    const n = pts.length;
    for (let i = 0; i < n; i++) {
      const p = pts[i], a = pts[(i - 1 + n) % n], b = pts[(i + 1) % n], r = radii[i];
      const da = Math.hypot(a.x - p.x, a.y - p.y), db = Math.hypot(b.x - p.x, b.y - p.y);
      const x1 = p.x + (a.x - p.x) * r / da, y1 = p.y + (a.y - p.y) * r / da;
      const x2 = p.x + (b.x - p.x) * r / db, y2 = p.y + (b.y - p.y) * r / db;
      if (i === 0) ctx.moveTo(tx(x1), ty(y1)); else ctx.lineTo(tx(x1), ty(y1));
      ctx.quadraticCurveTo(tx(p.x), ty(p.y), tx(x2), ty(y2));
    }
    ctx.closePath();
  }
  const roundedShape = (pts, radii) => { const s = new THREE.Shape(); roundedPath(s, pts, radii, v => v, v => v); return s; };
  const P = (x, y) => ({ x, y });
  const arrowPts = [P(-0.85,-3), P(0.85,-3), P(0.85,0), P(1.8,0), P(0,2.6), P(-1.8,0), P(-0.85,0)];
  const arrowR   = [0.38, 0.38, 0.12, 0.28, 0.32, 0.28, 0.12];
  const B = { x0: -1.8, x1: 1.8, y0: -3, y1: 2.6 };

  function frontTexture() {
    const W = 512, H = Math.round(512 * (B.y1 - B.y0) / (B.x1 - B.x0));
    const cv = document.createElement('canvas'); cv.width = W; cv.height = H;
    const g = cv.getContext('2d');
    const sx = x => (x - B.x0) / (B.x1 - B.x0) * W;
    const sy = y => (1 - (y - B.y0) / (B.y1 - B.y0)) * H;
    g.fillStyle = '#5aa84f'; g.fillRect(0, 0, W, H);
    g.beginPath(); roundedPath(g, arrowPts, arrowR, sx, sy);
    // preenchimento liso, igual à arte original
    g.fillStyle = '#8fd18a'; g.fill();
    g.save(); g.clip();
    // setinhas de reciclagem: traço grosso, verde um pouco mais escuro, ponta em "V" aberta.
    // Posições copiadas da arte original (coordenadas da imagem 584px convertidas para a seta 3D).
    const IX = px => sx((px - 300) / 111), IY = py => sy((305 - py) / 107.7), K = 1.28;
    g.strokeStyle = 'rgba(78,150,68,0.30)';
    g.lineWidth = 26; g.lineCap = 'round'; g.lineJoin = 'round';
    function arcArrow(cx, cy, r, a0, a1, head) {
      const ccw = a1 < a0;
      g.beginPath(); g.arc(IX(cx), IY(cy), r * K, a0, a1, ccw); g.stroke();
      if (!head) return;
      const ex = IX(cx) + Math.cos(a1) * r * K, ey = IY(cy) + Math.sin(a1) * r * K;
      const dir = a1 + (ccw ? -Math.PI / 2 : Math.PI / 2);   // direção em que a seta anda
      const len = 40;
      g.beginPath();
      g.moveTo(ex - Math.cos(dir - 0.75) * len, ey - Math.sin(dir - 0.75) * len);
      g.lineTo(ex, ey);
      g.lineTo(ex - Math.cos(dir + 0.75) * len, ey - Math.sin(dir + 0.75) * len);
      g.stroke();
    }
    arcArrow(222, 175, 85, 2.35, 0.12, true);    // em cima à esquerda, sobe pra direita
    arcArrow(385, 355, 75, -0.75, -2.95, true);  // no meio, desce pra esquerda
    arcArrow(425, 160, 52, 1.2, 3.4, false);     // pedaço cortado na borda direita
    arcArrow(200, 412, 52, -1.3, 1.3, false);    // pedaço cortado na lateral do corpo
    g.restore();
    g.beginPath(); roundedPath(g, arrowPts, arrowR, sx, sy);
    g.lineWidth = 30; g.strokeStyle = '#5aa84f'; g.stroke();
    const t = new THREE.CanvasTexture(cv);
    t.repeat.set(1 / (B.x1 - B.x0), 1 / (B.y1 - B.y0));
    t.offset.set(-B.x0 / (B.x1 - B.x0), -B.y0 / (B.y1 - B.y0));
    t.anisotropy = 4;
    return t;
  }

  // costas: verde liso com um símbolo de reciclagem no meio (três setas em círculo)
  function backTexture() {
    const W = 512, H = Math.round(512 * (B.y1 - B.y0) / (B.x1 - B.x0));
    const cv = document.createElement('canvas'); cv.width = W; cv.height = H;
    const g = cv.getContext('2d');
    const sx = x => (x - B.x0) / (B.x1 - B.x0) * W;
    const sy = y => (1 - (y - B.y0) / (B.y1 - B.y0)) * H;
    g.fillStyle = '#5aa84f'; g.fillRect(0, 0, W, H);
    g.beginPath(); roundedPath(g, arrowPts, arrowR, sx, sy);
    g.fillStyle = '#8fd18a'; g.fill();
    const cx = sx(0), cy = sy(-0.55), r = 0.62 * W / (B.x1 - B.x0);
    g.strokeStyle = 'rgba(78,150,68,0.42)'; g.lineWidth = 24; g.lineCap = 'round'; g.lineJoin = 'round';
    for (let k = 0; k < 3; k++) {
      const a0 = -Math.PI / 2 + k * 2 * Math.PI / 3 + 0.32, a1 = a0 + 1.45;
      g.beginPath(); g.arc(cx, cy, r, a0, a1); g.stroke();
      const ex = cx + Math.cos(a1) * r, ey = cy + Math.sin(a1) * r, dir = a1 + Math.PI / 2, len = 34;
      g.beginPath();
      g.moveTo(ex - Math.cos(dir - 0.75) * len, ey - Math.sin(dir - 0.75) * len);
      g.lineTo(ex, ey);
      g.lineTo(ex - Math.cos(dir + 0.75) * len, ey - Math.sin(dir + 0.75) * len);
      g.stroke();
    }
    g.beginPath(); roundedPath(g, arrowPts, arrowR, sx, sy);
    g.lineWidth = 30; g.strokeStyle = '#5aa84f'; g.stroke();
    const t = new THREE.CanvasTexture(cv);
    t.repeat.set(1 / (B.x1 - B.x0), 1 / (B.y1 - B.y0));
    t.offset.set(-B.x0 / (B.x1 - B.x0), -B.y0 / (B.y1 - B.y0));
    t.anisotropy = 4;
    return t;
  }

  const DEPTH = 0.55, BEV = 0.14;
  const arrowGeo = new THREE.ExtrudeGeometry(roundedShape(arrowPts, arrowR), {
    depth: DEPTH, bevelEnabled: true, bevelThickness: BEV, bevelSize: 0.1, bevelSegments: 4, curveSegments: 16
  });
  arrowGeo.translate(0, 0, -DEPTH / 2);
  // as duas tampas vêm num grupo só: a primeira metade é a de trás, a segunda é a da frente
  {
    const lids = arrowGeo.groups[0], half = lids.count / 2;
    arrowGeo.groups.splice(0, 1, { start: lids.start, count: half, materialIndex: 2 }, { start: lids.start + half, count: half, materialIndex: 0 });
  }
  const FRONT_Z = DEPTH / 2 + BEV;

  // ---------- hierarquia ----------
  const root = new THREE.Group();
  const bouncer = new THREE.Group();
  const body = new THREE.Group();
  scene.add(root); root.add(bouncer); bouncer.add(body);
  const FEET = -4.4;
  bouncer.position.y = FEET; body.position.y = -FEET - 0.4;

  const arrow = withOutline(new THREE.Mesh(arrowGeo, [toon(0xffffff, { map: frontTexture() }), toon(C.side), toon(0xffffff, { map: backTexture() })]), 0.07);
  body.add(arrow);

  // rosto
  const faceCv = document.createElement('canvas'); faceCv.width = 256; faceCv.height = 192;
  const fg = faceCv.getContext('2d');
  const faceTex = new THREE.CanvasTexture(faceCv);
  const face = new THREE.Mesh(new THREE.PlaneGeometry(1.6, 1.2), new THREE.MeshBasicMaterial({ map: faceTex, transparent: true }));
  face.position.set(0, -1.75, FRONT_Z + 0.005);
  body.add(face);

  function limb(w, h, color) {
    const g = new THREE.ExtrudeGeometry(roundedShape([P(-w/2,-h/2), P(w/2,-h/2), P(w/2,h/2), P(-w/2,h/2)], Array(4).fill(Math.min(w, h) * 0.35)), {
      depth: 0.22, bevelEnabled: true, bevelThickness: 0.06, bevelSize: 0.05, bevelSegments: 3, curveSegments: 8
    });
    g.translate(0, 0, -0.11);
    return withOutline(new THREE.Mesh(g, toon(color)), 0.045);
  }
  function makeArm(side) {
    const pivot = new THREE.Group();
    pivot.position.set(side * 1.32, -1.38, 0);
    const m = limb(0.72, 0.3, C.limb);
    m.position.x = side * 0.42;
    pivot.add(m);
    pivot.userData.base = side * -0.33;
    body.add(pivot);
    return pivot;
  }
  const armL = makeArm(-1), armR = makeArm(1);
  function makeLeg(x, rot) {
    const pivot = new THREE.Group();
    pivot.position.set(x, -3.45, 0);
    const m = limb(0.38, 1.0, C.limb);
    m.position.y = -0.6;
    const stripe = new THREE.Mesh(new THREE.PlaneGeometry(0.07, 0.62), new THREE.MeshBasicMaterial({ color: C.stripe }));
    stripe.position.set(0.02, -0.6, 0.19);
    pivot.add(m, stripe);
    pivot.userData.base = rot;
    body.add(pivot);
    return pivot;
  }
  const legL = makeLeg(-0.5, -0.32), legR = makeLeg(0.48, 0.16);

  // estrelinhas da tontura (escondidas até ele ficar tonto)
  const starShape = new THREE.Shape();
  for (let i = 0; i < 10; i++) { const r = i % 2 ? 0.11 : 0.26, a = Math.PI / 2 + i * Math.PI / 5; i ? starShape.lineTo(Math.cos(a) * r, Math.sin(a) * r) : starShape.moveTo(Math.cos(a) * r, Math.sin(a) * r); }
  const starGeo = new THREE.ExtrudeGeometry(starShape, { depth: 0.08, bevelEnabled: true, bevelThickness: 0.03, bevelSize: 0.03, bevelSegments: 2 });
  starGeo.center();
  const stars = new THREE.Group();
  for (let i = 0; i < 3; i++) {
    const st = new THREE.Mesh(starGeo, toon(0xf5d04a));
    const a = i * Math.PI * 2 / 3;
    const holder = new THREE.Group(); holder.position.set(Math.cos(a) * 1.4, 0, Math.sin(a) * 1.4);
    st.userData.holder = holder;
    holder.add(st); stars.add(holder);
  }
  stars.position.set(0, 3.15, 0); stars.visible = false;
  body.add(stars);
  armL.rotation.z = armL.userData.base; armR.rotation.z = armR.userData.base;
  legL.rotation.z = legL.userData.base; legR.rotation.z = legR.userData.base;

  // sombra de contato: elipse suave no chão, logo abaixo dos pés (que ficam em y ≈ -4.95)
  const GROUND = -5.05;
  const shCv = document.createElement('canvas'); shCv.width = 256; shCv.height = 256;
  const sg = shCv.getContext('2d');
  const srg = sg.createRadialGradient(128, 128, 0, 128, 128, 128);
  srg.addColorStop(0, 'rgba(4,10,6,0.8)');
  srg.addColorStop(0.35, 'rgba(4,10,6,0.55)');
  srg.addColorStop(0.7, 'rgba(4,10,6,0.16)');
  srg.addColorStop(1, 'rgba(6,14,9,0)');
  sg.fillStyle = srg; sg.fillRect(0, 0, 256, 256);
  const shadow = new THREE.Mesh(new THREE.PlaneGeometry(3.8, 2.4), new THREE.MeshBasicMaterial({ map: new THREE.CanvasTexture(shCv), transparent: true, depthWrite: false }));
  shadow.rotation.x = -Math.PI / 2; shadow.position.y = GROUND;
  shadow.renderOrder = -1;
  scene.add(shadow);

  // rosto: olhos em traço e sorrisinho de lado, com algumas expressões
  const F = { expr: 'normal', blink: false, lx: 0, ly: 0, talk: 0 };
  function drawFace() {
    const g = fg; g.clearRect(0, 0, 256, 192);
    g.strokeStyle = g.fillStyle = '#0f1a2e'; g.lineCap = 'round'; g.lineJoin = 'round';
    const ox = F.lx * 12, oy = F.ly * 9, e = F.expr;
    const L = [92 + ox, 74 + oy], R = [164 + ox, 74 + oy];
    g.lineWidth = 14;
    const bar = p => { g.beginPath(); g.moveTo(p[0], p[1] - 17); g.lineTo(p[0], p[1] + 17); g.stroke(); };
    const shut = p => { g.beginPath(); g.moveTo(p[0] - 14, p[1] + 4); g.lineTo(p[0] + 14, p[1] + 4); g.stroke(); };
    const happy = p => { g.beginPath(); g.moveTo(p[0] - 15, p[1] + 8); g.quadraticCurveTo(p[0], p[1] - 18, p[0] + 15, p[1] + 8); g.stroke(); };
    const round = p => { g.beginPath(); g.arc(p[0], p[1], 13, 0, Math.PI * 2); g.fill(); g.fillStyle = '#fff'; g.beginPath(); g.arc(p[0] + 4, p[1] - 5, 4, 0, Math.PI * 2); g.fill(); g.fillStyle = '#0f1a2e'; };
    const spiral = (p, dir) => { g.lineWidth = 6; g.beginPath(); for (let a = 0; a < 12; a += 0.25) { const r = a * 1.6, q = dir * (a + performance.now() / 110); const x = p[0] + Math.cos(q) * r, y = p[1] + Math.sin(q) * r; a ? g.lineTo(x, y) : g.moveTo(x, y); } g.stroke(); g.lineWidth = 14; };
    if (e === 'dizzy') { spiral(L, 1); spiral(R, -1); }
    else if (e === 'happy') { happy(L); happy(R); }
    else if (e === 'wink') { bar(L); happy(R); }
    else if (e === 'wow') { round(L); round(R); }
    else if (e === 'sleepy' || F.blink) { shut(L); shut(R); }
    else { bar(L); bar(R); }
    g.lineWidth = 10;
    const mx = 128 + ox * 0.5, my = 138 + oy * 0.5;
    if (e === 'happy') {
      g.beginPath(); g.moveTo(mx - 24, my - 8); g.quadraticCurveTo(mx, my - 12, mx + 24, my - 8);
      g.quadraticCurveTo(mx + 20, my + 22, mx, my + 22); g.quadraticCurveTo(mx - 20, my + 22, mx - 24, my - 8); g.fill();
    } else if (e === 'dizzy') {
      g.beginPath(); g.moveTo(mx - 24, my + 4);
      for (let i = 0; i <= 8; i++) g.lineTo(mx - 24 + i * 6, my + 4 + (i % 2 ? -5 : 5)); g.stroke();
    } else if (e === 'wow' || e === 'sleepy') {
      g.beginPath(); g.ellipse(mx, my + 4, 9, 12, 0, 0, Math.PI * 2); g.fill();
    } else if (F.talk) {
      g.beginPath(); g.ellipse(mx + 3, my + 4, 11, 5 + F.talk * 7, 0, 0, Math.PI * 2); g.fill();   // falando
    } else {
      g.beginPath(); g.moveTo(mx - 12, my - 4); g.bezierCurveTo(mx - 8, my + 20, mx + 22, my + 18, mx + 20, my - 12); g.stroke();
    }
    if (e === 'happy' || e === 'wink') {
      g.fillStyle = 'rgba(232,116,106,0.45)';
      g.beginPath(); g.ellipse(58 + ox, 116 + oy, 16, 9, 0, 0, Math.PI * 2); g.fill();
      g.beginPath(); g.ellipse(198 + ox, 116 + oy, 16, 9, 0, 0, Math.PI * 2); g.fill();
    }
    faceTex.needsUpdate = true;
  }
  drawFace();

  // animações que ele faz sozinho, uma de cada vez
  const ease = p => p < 0.5 ? 4 * p * p * p : 1 - Math.pow(-2 * p + 2, 3) / 2;
  const hump = p => Math.sin(Math.PI * Math.min(1, Math.max(0, p)));
  const ANIMS = {
    pulinho: { dur: 0.8, expr: 'happy', run(p, o) {
      if (p < 0.15) { const q = hump(p / 0.15); o.sy -= 0.16 * q; o.sx += 0.09 * q; }
      else if (p < 0.88) { const q = (p - 0.15) / 0.73, s = hump(q); o.y += 1.6 * 4 * q * (1 - q); o.sy += 0.07 * s; o.armL += 0.8 * s; o.armR += 0.8 * s; o.leg += 0.3 * s; }
      else { const q = hump((p - 0.88) / 0.12); o.sy -= 0.12 * q; o.sx += 0.07 * q; }
    } },
    pulinhoDuplo: { dur: 1.3, expr: 'happy', run(p, o) {
      const k = p < 0.5 ? p / 0.5 : (p - 0.5) / 0.5;
      const h = 4 * k * (1 - k);
      o.y += h * (p < 0.5 ? 0.9 : 1.3); o.armL += 0.6 * h; o.armR += 0.6 * h; o.leg += 0.25 * h;
      const land = Math.max(0, 1 - Math.min(k, 1 - k) * 12); o.sy -= 0.1 * land; o.sx += 0.06 * land;
    } },
    aceno: { dur: 1.8, expr: 'wink', run(p, o, t) {
      const env = hump(p); o.armR += env * (1.5 + Math.sin(t * 16) * 0.45); o.rz -= env * 0.08; o.yaw -= env * 0.3;
    } },
    giro: { dur: 1.2, expr: 'wow', run(p, o) {
      o.yaw += ease(p) * Math.PI * 2; o.y += hump(p) * 0.7; o.armL += hump(p) * 0.6; o.armR += hump(p) * 0.6;
    } },
    espreguica: { dur: 2.0, expr: 'sleepy', run(p, o) {
      const env = hump(p); o.armL += env * 1.7; o.armR += env * 1.7; o.sy += env * 0.1; o.sx -= env * 0.05; o.rz += Math.sin(p * Math.PI * 2) * 0.05 * env;
    } },
    olhaEmVolta: { dur: 2.4, expr: 'normal', run(p, o) {
      const side = p < 0.45 ? -1 : p < 0.9 ? 1 : 0;
      o.lx = side; o.ly = -0.2; o.yaw += side * 0.25 * hump(p * 1.1); o.rz += side * 0.04;
    } },
    dancinha: { dur: 2.6, expr: 'happy', run(p, o, t) {
      const env = Math.min(1, hump(p) * 2), b = t * 4.5;
      o.rz += Math.sin(b) * 0.2 * env; o.y += Math.abs(Math.sin(b)) * 0.35 * env;
      o.armL -= Math.sin(b) * 1.0 * env; o.armR += Math.sin(b) * 1.0 * env; o.leg += Math.sin(b) * 0.3 * env;
    } },
    puloAlto: { dur: 1.6, expr: 'wow', run(p, o) {
      if (p < 0.3) { const q = Math.sin(p / 0.3 * Math.PI / 2); o.sy -= 0.25 * q; o.sx += 0.12 * q; o.armL -= 0.4 * q; o.armR -= 0.4 * q; }
      else if (p < 0.9) { const q = (p - 0.3) / 0.6; o.y += 3.2 * 4 * q * (1 - q); o.yaw += ease(q) * Math.PI * 2; o.sy += 0.1 * hump(q); o.armL += 1.3 * hump(q); o.armR += 1.3 * hump(q); o.leg += 0.4 * hump(q); }
      else { const q = hump((p - 0.9) / 0.1); o.sy -= 0.2 * q; o.sx += 0.1 * q; }
    } }
  };
  // cócegas: só acontece quando você clica no corpo (não entra no sorteio)
  ANIMS.cocegas = { dur: 1.0, expr: 'happy', run(p, o) {
    const fade = 1 - p;
    o.y += 0.35 * hump(p / 0.45);
    o.sy -= 0.1 * Math.sin(p * Math.PI * 7) * fade; o.sx += 0.06 * Math.sin(p * Math.PI * 7) * fade;
    o.rz += Math.sin(p * Math.PI * 9) * 0.09 * fade;
    o.armL += 1.0 * hump(p); o.armR += 1.0 * hump(p); o.leg += Math.sin(p * Math.PI * 6) * 0.2 * fade;
  } };
  const names = Object.keys(ANIMS).filter(n => n !== 'cocegas');
  let current = null, curStart = 0, nextAt = 1.5, lastName = '';
  function pickNext() {
    let n; do { n = names[Math.floor(Math.random() * names.length)]; } while (n === lastName);
    if (reduceMotion) n = Math.random() < 0.5 ? 'olhaEmVolta' : 'aceno';
    lastName = n; return n;
  }

  // arrastar para girar, roda do mouse para aproximar
  const canvas = renderer.domElement;
  let spinMeter = 0, dizzyStart = -9, grabStart = -9;
  // giro: o arraste muda o alvo, e o corpo segue o alvo suavemente (sem trancos)
  let rotY = 0, rotX = 0, targetY = 0, targetX = 0, spinVel = 0, spinSmooth = 0, inertia = 0;
  let dragging = false, lx = 0, ly = 0, lastUp = -9, zoom = 1;
  const TILT = 0.35;
  // mãos e pés que dá pra pegar: cada um sabe pra que lado aponta quando está parado
  const limbs = [
    // lo/hi: até onde cada um pode girar sem entrar no corpo (nem um pé no outro)
    { pivot: armL, dir: Math.PI,      min: -2.6, max: 2.6, lo: -1.4, hi: 1.6,  kind: 'arm', side: -1 },
    { pivot: armR, dir: 0,            min: -2.6, max: 2.6, lo: -1.6, hi: 1.4,  kind: 'arm', side: 1 },
    { pivot: legL, dir: -Math.PI / 2, min: -1.3, max: 1.3, lo: -1.4, hi: 0.15, kind: 'leg', side: -1 },
    { pivot: legR, dir: -Math.PI / 2, min: -1.3, max: 1.3, lo: -0.15, hi: 1.4, kind: 'leg', side: 1 }
  ];
  const limbMeshes = [];
  limbs.forEach(L => { L.extra = 0; L.vel = 0; L.pivot.traverse(o => { if (o.isMesh && o.material.side !== THREE.BackSide) { o.userData.limb = L; limbMeshes.push(o); } }); });
  // área de toque maior e invisível em volta de cada mão e pé (no celular, maior ainda)
  const coarse = window.matchMedia('(pointer: coarse)').matches, HF = coarse ? 1.7 : 1.25;
  const hitMat = new THREE.MeshBasicMaterial({ visible: false });
  limbs.forEach(L => {
    const isArm = L.kind === 'arm';
    const box = new THREE.Mesh(new THREE.BoxGeometry((isArm ? 0.85 : 0.5) * HF, (isArm ? 0.45 : 1.15) * HF, 0.6), hitMat);
    if (isArm) box.position.x = L.side * 0.42; else box.position.y = -0.6;
    box.userData.limb = L; L.pivot.add(box); limbMeshes.push(box);
  });
  const bodyMeshes = [arrow, face];
  let downX = 0, downY = 0, downT = 0, moved = 0, onBody = false;
  const ray = new THREE.Raycaster(), ndc = new THREE.Vector2();
  let grab = null, grabX = 0, grabY = 0, limbGrabStart = -9;

  canvas.addEventListener('pointerdown', e => {
    const r = canvas.getBoundingClientRect();
    ndc.set(((e.clientX - r.left) / r.width) * 2 - 1, -((e.clientY - r.top) / r.height) * 2 + 1);
    ray.setFromCamera(ndc, camera);
    const hit = ray.intersectObjects(limbMeshes, false)[0];
    const bodyHit = ray.intersectObjects(bodyMeshes, false)[0];
    canvas.setPointerCapture(e.pointerId); canvas.classList.add('dragging');
    downX = e.clientX; downY = e.clientY; downT = performance.now() / 1000; moved = 0;
    onBody = !!bodyHit && (!hit || bodyHit.distance < hit.distance - 0.05);
    if (hit && !onBody) {
      grab = hit.object.userData.limb; grabX = e.clientX; grabY = e.clientY; limbGrabStart = performance.now() / 1000;
      return;
    }
    dragging = true; lx = e.clientX; ly = e.clientY; inertia = 0; targetY = rotY; grabStart = performance.now() / 1000;
  });
  canvas.addEventListener('pointermove', e => {
    moved = Math.max(moved, Math.hypot(e.clientX - downX, e.clientY - downY));
    if (grab) { grabX = e.clientX; grabY = e.clientY; return; }
    if (!dragging) return;
    const dx = e.clientX - lx, dy = e.clientY - ly; lx = e.clientX; ly = e.clientY;
    targetY += dx * 0.012;
    targetX = Math.max(-TILT, Math.min(TILT, targetX + dy * 0.004));
  });
  const end = () => {
    if (grab) { grab.vel = -grab.extra * 4; grab = null; releasedAt = performance.now() / 1000; }   // solta: o membro volta balançando
    const now = performance.now() / 1000;
    if (dragging && onBody && moved < 8 && now - downT < 0.45) {
      // foi um clique rápido no corpo, não um arraste: cócegas!
      current = 'cocegas'; curStart = now; inertia = 0; targetY = rotY;
      if (window.voltinhaClique) setTimeout(window.voltinhaClique, 700);
    } else if (dragging) inertia = Math.max(-14, Math.min(14, spinSmooth));   // continua girando com a velocidade do arraste
    dragging = false; lastUp = performance.now() / 1000; canvas.classList.remove('dragging');
  };
  canvas.addEventListener('pointerup', end);
  // ---------- reação à rolagem da página ----------
  let lastScroll = window.scrollY, scrollVel = 0, scrollSmooth = 0, scrollMeter = 0, lastScrollT = -9;
  window.addEventListener('scroll', () => {
    const y = window.scrollY, now = performance.now() / 1000;
    scrollMeter += Math.abs(y - lastScroll);
    lastScroll = y; lastScrollT = now;
  }, { passive: true });
  let scrollPrev = window.scrollY;
  // quando uma seção nova aparece, ele faz uma animação e fala alguma coisa
  window.voltinhaReage = (anim, falas) => {
    const now = performance.now() / 1000;
    if (falas) say(falas);
    if (anim && ANIMS[anim] && !dragging && !grab && (now - dizzyStart > 3.6)) { current = anim; curStart = now; }
  };
  // ---------- balão de fala: mostra uma frase de cada vez ----------
  const bubble = document.getElementById('voltinha-fala');
  const bubbleTxt = document.getElementById('voltinha-fala-texto');
  const bubbleCont = document.getElementById('voltinha-fala-cont');
  let bubbleTimer = 0, fila = [], pos = 0;
  let falando = false;
  function mostrar() {
    clearTimeout(bubbleTimer);
    if (pos >= fila.length) { bubble.classList.remove('visivel'); falando = false; return; }
    const txt = fila[pos];
    bubbleTxt.textContent = txt;
    bubbleCont.textContent = fila.length > 1 ? (pos + 1) + '/' + fila.length : '';
    bubble.classList.remove('pop'); void bubble.offsetWidth; bubble.classList.add('visivel', 'pop');
    falando = true;
    // tempo para ler: ~65 ms por letra, entre 3 e 9 segundos
    bubbleTimer = setTimeout(() => { pos++; mostrar(); }, Math.max(3000, Math.min(9000, txt.length * 65)));
  }
  function say(txt) {
    if (!bubble) return;
    fila = Array.isArray(txt) ? txt : [txt]; pos = 0; mostrar();
  }
  // clicar no balão passa para a próxima frase
  if (bubble) bubble.addEventListener('click', () => { pos++; mostrar(); });
  window.voltinhaDiz = say;
  canvas.addEventListener('pointercancel', end);
  let releasedAt = -9;
  const wp = new THREE.Vector3();
  function pivotOnScreen(pivot) {
    pivot.getWorldPosition(wp); wp.project(camera);
    const r = canvas.getBoundingClientRect();
    return [(wp.x * 0.5 + 0.5) * r.width + r.left, (-wp.y * 0.5 + 0.5) * r.height + r.top];
  }
  const wrap = a => Math.atan2(Math.sin(a), Math.cos(a));

  function resize() {
    const w = stage.clientWidth, h = stage.clientHeight;
    renderer.setSize(w, h, false);
    camera.aspect = w / h;
    const dist = 16 * zoom * Math.max(1, 0.8 / camera.aspect);
    camera.position.set(0, 2.6, dist);
    camera.lookAt(0, -1.3, 0);
    camera.updateProjectionMatrix();
  }
  window.addEventListener('resize', resize); resize();

  // o giro acontece em volta do meio do corpo, não dos pés
  const PIV = -2.0;
  root.position.y = PIV;
  const POSE = { y: 0, sx: 1, sy: 1, rz: 0, yaw: 0, armL: 0, armR: 0, leg: 0 };
  let blinkNext = 2, last = performance.now() / 1000;
  function frame() {
    const t = performance.now() / 1000, dt = Math.min(0.05, t - last); last = t;
    if (!dragging) {
      targetY += inertia * dt; inertia *= Math.exp(-2.6 * dt);
      if (Math.abs(inertia) < 0.05) inertia = 0;
      if (t - lastUp > 1.5) targetX += (0 - targetX) * (1 - Math.exp(-3 * dt));
    }
    const prevY = rotY;
    const follow = 1 - Math.exp(-(dragging ? 22 : 14) * dt);
    rotY += (targetY - rotY) * follow;
    rotX += (targetX - rotX) * follow;
    spinVel = dt > 0 ? (rotY - prevY) / dt : 0;                 // velocidade real do giro (rad/s)
    spinSmooth += (spinVel - spinSmooth) * (1 - Math.exp(-10 * dt));
    spinMeter += Math.abs(rotY - prevY);

    // tontura: girou muitas voltas em pouco tempo
    spinMeter = Math.max(0, spinMeter - dt * 1.8);
    const DIZZY_DUR = 3.6;
    if (spinMeter > Math.PI * 3.5 && t - dizzyStart > DIZZY_DUR) {
      dizzyStart = t; spinMeter = 0; current = null; nextAt = t + DIZZY_DUR + 1.5;
    }
    const dizzy = (t - dizzyStart) / DIZZY_DUR;
    const isDizzy = dizzy >= 0 && dizzy < 1;
    if (isDizzy && !current) nextAt = Math.max(nextAt, t + 1);

    // velocidade da rolagem (px/s), suavizada
    const sy = window.scrollY;
    scrollVel = dt > 0 ? (sy - scrollPrev) / dt : 0; scrollPrev = sy;
    scrollSmooth += (scrollVel - scrollSmooth) * (1 - Math.exp(-8 * dt));
    scrollMeter = Math.max(0, scrollMeter - dt * 1400);
    // rolou muito rápido por muito tempo: fica tonto
    if (scrollMeter > 7000 && t - dizzyStart > DIZZY_DUR) {
      dizzyStart = t; scrollMeter = 0; current = null; nextAt = t + DIZZY_DUR + 1.5; say('Opa... rolou rápido demais! 😵');
    }
    const scrolling = Math.abs(scrollSmooth) > 40;

    // espera um pouco depois de você mexer nele antes de voltar às animações
    if (dragging || grab || scrolling || Math.abs(spinSmooth) > 0.5) nextAt = Math.max(nextAt, t + 2);
    // escolhe e roda a animação da vez
    if (!current && !dragging && !grab && !isDizzy && t > nextAt) { current = pickNext(); curStart = t; }
    const o = { y: 0, sx: 1, sy: 1, rz: 0, yaw: 0, armL: 0, armR: 0, leg: 0, lx: 0, ly: 0 };
    F.expr = 'normal';
    if (current && (dragging || isDizzy || grab)) current = null;
    if (current) {
      const a = ANIMS[current], p = (t - curStart) / a.dur;
      if (p >= 1) { current = null; nextAt = t + 2.5 + Math.random() * 2.5; }
      else { a.run(p, o, t); F.expr = a.expr; }
    }
    const idle = reduceMotion ? 0 : 1;
    o.y += Math.sin(t * 2) * 0.06 * idle;
    o.armL += Math.sin(t * 2 + 1) * 0.06 * idle; o.armR -= Math.sin(t * 2 + 1) * 0.06 * idle;
    if (!current) { o.lx = Math.sin(t * 0.5) * 0.3; }

    // reações à rolagem: inclina, olha pra onde a página vai e se segura com os braços
    if (scrolling && !dragging && !grab) {
      const k = Math.max(-1, Math.min(1, scrollSmooth / 2500));
      const ak = Math.abs(k);
      o.ly = Math.max(-1, Math.min(1, k * 1.6));               // rolando pra baixo, olha pra baixo
      o.rz += k * 0.12;
      o.y += Math.abs(Math.sin(t * 14)) * 0.18 * ak;            // tremidinha de vento
      o.armL += 1.2 * ak; o.armR += 1.2 * ak;
      o.sy += (k > 0 ? -0.06 : 0.06) * ak;
      if (!current) F.expr = ak > 0.55 ? 'wow' : 'normal';
    }

    // reações ao ser girado
    const speed = Math.abs(spinSmooth);
    if (speed > 0.3 || dragging) {
      const k = Math.min(1, speed / 8);
      o.armL += 1.1 * k; o.armR += 1.1 * k;                                   // braços abrem com a força do giro
      o.rz -= Math.max(-0.22, Math.min(0.22, spinSmooth * 0.03));             // corpo inclina pro lado do giro
      o.lx = Math.max(-1, Math.min(1, -spinSmooth * 0.12));                   // olhos ficam pra trás
    }
    if (dragging) {
      // susto ao ser agarrado, depois diversão quando está girando rápido
      if (t - grabStart < 0.45) F.expr = 'wow';
      else if (spinMeter > Math.PI * 2.5) F.expr = 'wow';
      else if (speed > 2.5) F.expr = 'happy';
    } else if (speed > 2.5) F.expr = 'happy';
    // segurando uma mão ou um pé: ele se inclina junto e reage
    let pull = 0, pullUp = 0;
    if (grab) {
      const [px, py] = pivotOnScreen(grab.pivot);
      const r = canvas.getBoundingClientRect();
      pull = Math.max(-1, Math.min(1, (grabX - px) / (r.width * 0.25)));
      pullUp = Math.max(-1, Math.min(1, (py - grabY) / (r.height * 0.25)));
      const held = t - limbGrabStart;
      F.expr = held < 0.45 ? 'wow' : (Math.hypot(pull, pullUp) > 0.6 ? 'wow' : 'happy');
      o.lx = Math.max(-1, Math.min(1, pull * 1.5)); o.ly = Math.max(-1, Math.min(1, -pullUp * 1.5));  // olha pra mão que puxa
      o.rz -= pull * 0.22;                                            // corpo vai junto pro lado do puxão
      if (grab.kind === 'arm') o.y += Math.max(0, pullUp) * 0.45;     // puxar a mão pra cima levanta ele
      else { o.y += Math.max(0, pullUp) * 0.25; o.sy -= Math.max(0, -pullUp) * 0.08; }
      if (grab.kind === 'arm') { if (grab.side < 0) o.armR += 0.4; else o.armL += 0.4; }  // o outro braço se equilibra
      else o.leg += -pull * 0.25 * grab.side;
    } else if (t - releasedAt < 0.6) {
      F.expr = 'happy';
    }
    if (isDizzy && !dragging) {
      const env = Math.min(1, Math.sin(dizzy * Math.PI) * 2);
      if (dizzy < 0.82) {
        F.expr = 'dizzy';
        o.rz += Math.sin(t * 5) * 0.2 * env;          // cambaleia
        o.yaw += Math.sin(t * 2.5) * 0.35 * env;
        o.sy -= 0.05 * env; o.sx += 0.03 * env;
        o.armL += (0.5 + Math.sin(t * 5) * 0.4) * env; o.armR += (0.5 - Math.sin(t * 5) * 0.4) * env;
        o.leg += Math.sin(t * 5) * 0.25 * env;
      } else {
        // balança a cabeça pra acordar e volta ao normal
        const q = (dizzy - 0.82) / 0.18;
        F.expr = q < 0.6 ? 'sleepy' : 'happy';
        o.yaw += Math.sin(q * Math.PI * 6) * 0.3 * (1 - q);
      }
    }

    if (falando && !current && !dragging && !grab && !isDizzy) { o.armR += 0.35 + Math.sin(t * 3) * 0.25; o.rz += Math.sin(t * 1.7) * 0.03; }

    // a pose final passa por um amortecedor: quando uma animação é interrompida, ele não dá tranco
    const a = 1 - Math.exp(-20 * dt);
    for (const k in o) { if (k === 'lx' || k === 'ly') continue; POSE[k] += (k === 'yaw' ? wrap(o[k] - POSE[k]) : o[k] - POSE[k]) * a; }
    root.rotation.set(rotX, rotY + POSE.yaw, 0);
    bouncer.position.y = FEET - PIV + POSE.y;
    bouncer.scale.set(POSE.sx, POSE.sy, POSE.sx);
    bouncer.rotation.z = POSE.rz;
    armL.rotation.z = armL.userData.base - POSE.armL;
    armR.rotation.z = armR.userData.base + POSE.armR;
    legL.rotation.z = legL.userData.base + POSE.leg;
    legR.rotation.z = legR.userData.base - POSE.leg;
    // membro segurado aponta pro ponteiro; os soltos voltam como mola
    const mirrored = Math.cos(rotY + POSE.yaw) < 0;
    limbs.forEach(L => {
      if (grab === L) {
        const [px, py] = pivotOnScreen(L.pivot);
        let ang = Math.atan2(-(grabY - py), grabX - px);
        if (mirrored) ang = Math.PI - ang;
        const want = Math.max(L.min, Math.min(L.max, wrap(ang - L.dir - POSE.rz)));
        const target = wrap(want - L.pivot.rotation.z);
        L.extra += (target - L.extra) * 0.35; L.vel = 0;
      } else if (L.extra !== 0 || L.vel !== 0) {
        L.vel += (-140 * L.extra - 7 * L.vel) * dt;
        L.extra += L.vel * dt;
        if (Math.abs(L.extra) < 0.001 && Math.abs(L.vel) < 0.01) { L.extra = 0; L.vel = 0; }
      }
      L.pivot.rotation.z += L.extra;
      // trava no limite: a mão e o pé param na borda do corpo em vez de atravessar
      const rz = L.pivot.rotation.z, c = Math.max(L.lo, Math.min(L.hi, rz));
      if (c !== rz) {
        L.pivot.rotation.z = c;
        if (grab !== L) { L.extra += c - rz; L.vel *= -0.3; }   // bate no limite e volta um pouco
      }
    });
    // quanto mais alto ele pula, menor e mais clara a sombra
    const lift = Math.max(0, POSE.y);
    shadow.scale.set(1 / (1 + lift * 0.35), 1 / (1 + lift * 0.35), 1);
    shadow.material.opacity = 1 / (1 + lift * 0.6);

    // estrelinhas rodando em volta da cabeça quando está tonto
    const showStars = isDizzy && dizzy < 0.85;
    stars.visible = showStars;
    if (showStars) {
      stars.rotation.y = t * 3;
      stars.children.forEach((h, i) => { h.position.y = Math.sin(t * 4 + i * 2) * 0.12; const st = h.children[0]; st.lookAt(camera.position); st.rotateZ(t * 4 + i); });
      stars.scale.setScalar(Math.min(1, dizzy * 8, (0.85 - dizzy) * 8));
    }
    // boca mexendo e um gesto leve enquanto fala
    F.talk = falando && (F.expr === 'normal' || F.expr === 'wink') ? Math.max(0, Math.sin(t * 13) * Math.sin(t * 5.3 + 1)) : 0;
    F.lx += (o.lx - F.lx) * 0.12; F.ly += (o.ly - F.ly) * 0.12;
    if (t > blinkNext) { F.blink = true; if (t > blinkNext + 0.12) { F.blink = false; blinkNext = t + 2 + Math.random() * 3; } }
    drawFace();

    renderer.render(scene, camera);
    if (!fallback.hidden) fallback.hidden = true;
    requestAnimationFrame(frame);
  }
  requestAnimationFrame(frame);
})();

// ---------- Voltinha guiando a visita pela landing page ----------
(function () {
  const sobre = document.querySelectorAll('#sobre-nos .sobre-item');
  if (sobre[0]) sobre[0].id = sobre[0].id || 'sobre-plataforma';
  if (sobre[1]) sobre[1].id = sobre[1].id || 'sobre-chat';

  // o que ele explica em cada parte da página (uma frase por balão)
  const roteiro = {
    'painel': ['aceno', [
      'Oi! Eu sou o Voltinha, o guia do VOLTA!',
      'O VOLTA conecta empresas que geram recicláveis com cooperativas de coleta.',
      'Role a página que eu explico cada parte. Clique em mim para repetir!'
    ]],
    'sobre-plataforma': ['pulinho', [
      'Aqui é o "Sobre Nós".',
      'No mapa do app, a empresa acha cooperativas da região e vê a avaliação delas.',
      'Tudo rápido e com uma interface simples.'
    ]],
    'sobre-chat': ['olhaEmVolta', [
      'E tem chat integrado!',
      'Empresa e cooperativa combinam a coleta ali mesmo, de forma simples e segura.'
    ]],
    'empresas': ['pulinhoDuplo', [
      'Agora o "Como Funciona" para empresas.',
      'São 7 passos, do cadastro até o dia da coleta.',
      'Use as setinhas que eu conto cada passo!'
    ]],
    'cooperativas': ['aceno', [
      'Essa parte é para as cooperativas.',
      'Elas se cadastram, dizem que resíduo recolhem e esperam o contato das empresas.',
      'Depois é só conversar e confirmar a coleta. São 4 passos!'
    ]],
    'dashboard': ['giro', [
      'Olha os dashboards!',
      'Dá pra ver o histórico das coletas e o status das atuais.',
      'E ainda tem um painel de métricas ESG!'
    ]],
    'objetivo': ['espreguica', [
      'Nosso objetivo:',
      'ligar empresas e cooperativas de um jeito fácil e rápido.',
      'E facilitar a coleta e a triagem com relatórios completos.'
    ]],
    'equipe': ['dancinha', [
      'Essa é a equipe que criou o VOLTA (e eu também)!',
      'Tem gente de front-end, back-end, design UI/UX e dados.'
    ]],
    'footer': ['aceno', [
      'Chegamos ao fim!',
      'Aqui ficam o Instagram, o GitHub e atalhos para cada parte.',
      'Para começar, clique em Login. Até a próxima volta! 👋'
    ]]
  };
  const ids = Object.keys(roteiro);
  const explicados = new Set();
  let atual = null;

  function explicar(id, forcar) {
    if (!roteiro[id] || !window.voltinhaReage) return;
    const [anim, falas] = roteiro[id];
    window.voltinhaReage(explicados.has(id) && !forcar ? null : anim, falas);
    explicados.add(id);
  }

  // a seção "atual" é a que está passando pelo meio da tela
  let espera = 0;
  const io = new IntersectionObserver(entries => {
    entries.forEach(en => {
      if (!en.isIntersecting) return;
      const id = en.target.id;
      if (id === atual) return;
      atual = id;
      clearTimeout(espera);
      // só explica quando a pessoa para um pouquinho na seção (e só na primeira vez)
      if (!explicados.has(id)) espera = setTimeout(() => { if (atual === id) explicar(id); }, id === 'painel' ? 900 : 450);
    });
  }, { rootMargin: '-45% 0px -45% 0px' });
  ids.forEach(id => { const el = document.getElementById(id); if (el) io.observe(el); });

  // clicou nele: repete a explicação da parte em que você está
  window.voltinhaClique = () => { if (atual) explicar(atual, true); };

  // setinhas do "Como Funciona": ele lê o passo que apareceu
  function lerPasso(tituloId, blocos, nome) {
    const t = document.getElementById(tituloId);
    if (!t) return;
    const texto = blocos.map(b => (document.getElementById(b) || {}).innerText || '').filter(Boolean);
    window.voltinhaDiz && window.voltinhaDiz([nome + ': ' + t.innerText + '!'].concat(texto));
  }
  const passos = [
    ['btn-avancar', 'btn-voltar', 'slider-titulo-empresa', ['bloco-um', 'bloco-dois', 'bloco-tres'], 'Empresa'],
    ['btn-avancar-coop', 'btn-voltar-coop', 'slider-titulo-coop', ['bloco-um-coop', 'bloco-dois-coop', 'bloco-tres-coop'], 'Cooperativa']
  ];
  passos.forEach(([a, v, tit, blocos, nome]) => [a, v].forEach(bid => {
    const b = document.getElementById(bid);
    if (b) b.addEventListener('click', () => setTimeout(() => {
      lerPasso(tit, blocos, nome);
      window.voltinhaReage && window.voltinhaReage('pulinho');
    }, 0));
  }));

  // botão de esconder / mostrar
  const box = document.getElementById('voltinha');
  const btn = document.getElementById('voltinha-fechar');
  if (box && btn) btn.addEventListener('click', () => {
    const fechado = box.classList.toggle('minimizado');
    btn.setAttribute('aria-label', fechado ? 'Mostrar o Voltinha' : 'Esconder o Voltinha');
    btn.title = fechado ? 'Chamar o Voltinha' : '';
  });
})();