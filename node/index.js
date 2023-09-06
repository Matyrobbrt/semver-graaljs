const semver = require('semver')

export function compare(ver1, ver2) {
	return semver.compare(ver1, ver2)
}

export function satisfies(version, range) {
	return semver.satisfies(version, range)
}

export const RELEASE_TYPES = semver.RELEASE_TYPES