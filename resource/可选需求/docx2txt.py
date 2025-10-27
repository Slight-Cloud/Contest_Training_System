import os
from docx import Document

folder = r'd:\桌面1\可选需求'
for filename in os.listdir(folder):
    if filename.endswith('.docx') and not filename.startswith('~$'):
        docx_path = os.path.join(folder, filename)
        txt_path = os.path.join(folder, filename.replace('.docx', '.txt'))
        doc = Document(docx_path)
        text = '\n'.join([para.text for para in doc.paragraphs])
        with open(txt_path, 'w', encoding='utf-8') as f:
            f.write(text)
print('转换完成！')
