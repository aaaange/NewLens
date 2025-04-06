import os
import asyncio
import redis.asyncio as redis


class RedisClient:
    def __init__(self):
        redis_host = os.getenv("REDIS_HOST", "localhost")
        redis_port = os.getenv("REDIS_PORT", "6379")
        self.redis_url = f"redis://{redis_host}:{redis_port}"
        self.redis = None

    async def get_redis(self):
        if self.redis is None:
            self.redis = redis.from_url(self.redis_url)
        return self.redis

    async def is_duplicate(self, set_name: str, key: str) -> bool:
        redis_conn = await self.get_redis()
        return await redis_conn.sismember(set_name, key)

    async def add_key(self, set_name: str, key: str):
        redis_conn = await self.get_redis()
        await redis_conn.sadd(set_name, key)


if __name__ == "__main__":

    async def test_redis():
        client = RedisClient()
        duplicate = await client.is_duplicate("test_set", "key1")
        print("중복 여부 (expect False):", duplicate)
        await client.add_key("test_set", "key1")
        duplicate = await client.is_duplicate("test_set", "key1")
        print("중복 여부 (expect True):", duplicate)
        redis_conn = await client.get_redis()
        await redis_conn.aclose()

    asyncio.run(test_redis())
