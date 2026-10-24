import { cp, mkdir, readFile, readdir, rm, writeFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('.', import.meta.url));
const source = `${root}src`;
const output = `${root}dist`;
await rm(output, { recursive: true, force: true });
await mkdir(output, { recursive: true });

const components = {};
for (const name of ['header', 'footer']) {
  components[name] = await readFile(`${source}/components/${name}.html`, 'utf8');
}
for (const page of await readdir(`${source}/pages`)) {
  let html = await readFile(`${source}/pages/${page}`, 'utf8');
  html = html.replace(/<!-- component:(\w+) -->/g, (_, name) => {
    if (!components[name]) throw new Error(`Unknown component: ${name}`);
    return components[name];
  });
  if (/\bth:|\$\{|<!-- component:/.test(html)) {
    throw new Error(`Unresolved template in ${page}`);
  }
  await writeFile(`${output}/${page}`, html);
}
for (const folder of ['scripts', 'styles', 'images']) {
  await cp(`${source}/${folder}`, `${output}/${folder}`, { recursive: true });
}
console.log('Built standalone frontend in dist');
