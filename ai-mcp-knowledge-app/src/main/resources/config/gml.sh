curl --request POST \
  --url https://open.bigmodel.cn/api/paas/v4/chat/completions \
  --header 'Authorization: Bearer c3e3912426c49a7b990280b1d2d235a3.tcgJOlShzn6ba6uk' \
  --header 'Content-Type: application/json' \
  --data '{
  "model": "GLM-4-Flash-250414",
  "messages": [
    {
      "role": "user",
      "content": "What opportunities and challenges will the Chinese large model industry face in 2025?"
    }
  ]
}'