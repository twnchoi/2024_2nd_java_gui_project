## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


## TODO

1. 탐색기 구현
    - 디렉토리 선택 후 트리 구조 표시
    - 리프레시
    - 각 디렉토리/파일 별 크기와 비율 표시
    - 파일 삭제 후 자동 리프레시
2. 인덱싱 구현
    - 파일 별 구조체로 저장
    - 크기, 확장자, 이름, 해쉬
    - 빠르게 검색
3. 분석 툴
    - 확장자 별 크기와 개수
    - 일정 크기 이상/이하의 파일 검출
    - 중복 파일 검출
    - 빈 폴더 검출
