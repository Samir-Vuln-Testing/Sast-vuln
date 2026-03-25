import * as fs from 'fs';
const secretPath = '/secret';
const secret = fs.readFileSync(`${secretPath}/secret.txt`, 'utf-8');
console.log(${secret});