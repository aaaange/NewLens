import json
from hdfs import InsecureClient


def test_hdfs_write():
    # 1) HDFS 클라이언트 생성
    #    - 첫 번째 인자: "http://<호스트>:<포트>"
    #    - 'localhost' 대신 '127.0.0.1'을 써도 됨
    #    - user='hdfs' 혹은 user='root' 등은 Hadoop 설정에 따라 다를 수 있음
    client = InsecureClient("http://localhost:9870", user="root")

    # 2) 간단한 JSON 데이터 준비
    data = {"message": "Hello from Python to HDFS (Docker)", "status": "success"}

    # 3) HDFS 경로 설정
    hdfs_path = "/tmp/python_to_hdfs2.json"

    # (선택) 디렉토리 없으면 생성
    client.makedirs("/tmp")

    # 4) HDFS에 직접 쓰기 (덮어쓰기 가능하도록 overwrite=True)
    with client.write(hdfs_path, encoding="utf-8", overwrite=True) as writer:
        writer.write(json.dumps(data))

    print(f"Data successfully written to {hdfs_path} in HDFS!")


if __name__ == "__main__":
    test_hdfs_write()
