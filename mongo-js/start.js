/*
Copyright 2016 SynTouch B.V.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

// switch to test db
use test

// drop test collection
db.test.drop()

// optionaly create test collection
db.createCollection('test')

// create JSON document and save it (save will add the _id to the original JSON document)
testDoc={'field1':'value1'}
db.test.save(testDoc)

// query for _id
db.test.find({'_id':testDoc._id})

// query for field1
db.test.find({'field1':'value1'})
