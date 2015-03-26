module LastFmImporter 

open System
open FSharp.Data
open HttpClient
open Microsoft.FSharp.Control

// Build our types based on samples of the JSON we expect.
type LastFmApiCredentials = JsonProvider<"data/lastfm/credentials.sample.json">
type LastFmTracks = JsonProvider<"data/lastfm/user.getTopTracks.json">
type LastFmUser = JsonProvider<"data/lastfm/user.getInfo.json">

// You need to provide this file with a valid API key for this to work
let loadApiKey () =
    LastFmApiCredentials.Load(
        __SOURCE_DIRECTORY__ + "/data/lastfm/credentials.json").ApiKey

type User(name: string, trackCount: int) =
    member m.Name = name
    member m.TrackCount = trackCount

type Artist(name: string,
            mbid: Guid option) =
    member m.Name = name
    member m.Mbid = mbid

type Track(name: string,
           artist: Artist,
           mbid: Guid option) =
    member m.Name = name
    member m.Artist = artist
    member m.Mbid = mbid

let baseUri = "http://ws.audioscrobbler.com/2.0/"


let buildUserInfoRequest(username: string, apiKey: string) =
    createRequest Get baseUri
    |> withQueryStringItem { name="api_key"; value=apiKey }  
    |> withQueryStringItem { name="format"; value="json" }  
    |> withQueryStringItem { name="method"; value="user.getInfo" }  
    |> withQueryStringItem { name="user"; value=username }  

let buildGetTracksRequest(username: string, apiKey: string) =
    createRequest Get baseUri
    |> withQueryStringItem { name="api_key"; value=apiKey }  
    |> withQueryStringItem { name="format"; value="json" }  
    |> withQueryStringItem { name="method"; value="user.getTopTracks" }  
    |> withQueryStringItem { name="user"; value=username }
    |> withQueryStringItem { name="page"; value="1" }


// Gets some user information from the Last.fm API
let getUser(username: string):Async<User> = async {
    let apiKey = loadApiKey ()
    let request = buildUserInfoRequest(username, apiKey)
    let! response = (request |> getResponseBodyAsync)
    let json = LastFmUser.Parse(response)
    return User(json.User.Name, json.User.Playcount)
}

let parseArtist(artist: LastFmTracks.Artist) = 
    new Artist(artist.Name, artist.Mbid)

// Parse the JSON to a tracks object
let parseTracks(tracks: LastFmTracks.Root) = seq {    
    for track in tracks.Toptracks.Track do
        let artist = parseArtist(track.Artist)
        yield new Track(track.Name, artist, track.Mbid)
}

// Gets all the user's tracks
let getTracksForUser(username: String) = async {
    let apiKey = loadApiKey ()
    let! user = getUser(username)
    let request = buildGetTracksRequest(username, apiKey)
    let! response = (request |> getResponseBodyAsync)
    let json = LastFmTracks.Parse(response)
    return parseTracks(json)
}
